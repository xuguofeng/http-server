package org.net5ijy.nio.http.response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.net5ijy.nio.http.config.ContentTypeUtil;
import org.net5ijy.nio.http.config.HttpServerConfig;
import org.net5ijy.nio.http.config.ResponseUtil;
import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.response.view.ViewResovler;
import org.net5ijy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse implements Response {

	private static Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	private static String viewTemplateDir;

	private CharsetEncoder encoder;

	private String contentType = "text/html;charset=utf-8";

	private int status = 0;

	private Map<String, String> headers = new HashMap<String, String>();

	private List<Cookie> cookies = new ArrayList<Cookie>();

	private FileChannel in;
	private SocketChannel out;

	private StringBuilder content = new StringBuilder();

	private String html = null;

	// 获取服务器配置
	static HttpServerConfig config = HttpServerConfig.getInstance();

	private Request req;

	static {
		viewTemplateDir = config.getTemplateDir();
	}

	/**
	 * 根据指定的Request和客户端SocketChannel创建HttpResponse<br />
	 * <br />
	 * 内部会获取uri对应的本地资源<br />
	 * <br />
	 * 设置Content-Type、添加响应header<br />
	 * <br />
	 * 
	 * @param req
	 *            - 请求对象
	 * @param sChannel
	 *            - 客户端通道
	 * @param isStatic
	 *            - 是否是静态资源请求
	 */
	public HttpResponse(Request req, SocketChannel sChannel, boolean isStatic) {

		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

		// 获取字符集
		setCharsetEncoding(config.getResponseCharset());

		// 获取Content-Type
		setContentType(ContentTypeUtil.getContentType(ContentTypeUtil.HTML));
		this.headers.put("Date", sdf.format(new Date()));
		this.headers.put("Server", "nginx");
		this.headers.put("Connection", "keep-alive");
		// 客户端输出通道
		this.out = sChannel;

		this.req = req;

		// 静态资源
		if (isStatic) {
			// 获取请求资源URI
			String uri = req.getRequestURI();

			// 获取本地输入通道
			getLocalFileChannel(uri);

			// 设置Content-Type
			setContentType(req.getContentType());

			// 设置静态资源过期响应头
			int expires = config.getExpiresMillis(this.contentType);
			if (expires > 0) {
				long expiresTimeStamp = System.currentTimeMillis() + expires;
				this.headers.put("Expires",
						sdf.format(new Date(expiresTimeStamp)));
			}
		}
	}

	/**
	 * 从请求的资源uri获取本地文件输入通道<br />
	 * <br />
	 * 方法内部会根据是否获取到输入通道设置响应码200或404<br />
	 * <br />
	 * 如果资源404会响应一个默认的404.html<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:42:21
	 * @param uri
	 *            - 请求的uri
	 */
	private void getLocalFileChannel(String uri) {
		// 打开本地文件
		try {

			// 获取资源文件的Path
			Path path = Paths.get(config.getRoot(), uri);
			// 获取封装过的文件时间信息
			FileTime ft = Files.getLastModifiedTime(path,
					LinkOption.NOFOLLOW_LINKS);
			// 获取文件最后修改时间的时间戳
			long t1 = ft.toMillis();
			String lastModifyDate = sdf.format(new Date(t1));

			// 从请求头获取If-Modified-Since
			String IfModifiedSince = this.req.getHeader("If-Modified-Since");

			// 文件最后修改时间没有变化
			if (lastModifyDate.equals(IfModifiedSince)) {
				// 设置304
				// 设置响应头Last-Modified
				this.headers.put("Last-Modified", IfModifiedSince);
				setResponseCode(ResponseUtil.RESPONSE_CODE_304);
				return; // 返回，不去打开文件通道了
			}

			this.in = FileChannel.open(path, StandardOpenOption.READ);
			// 设置Content-Length响应头
			setHeader("Content-Length", String.valueOf(in.size()));

			// 设置响应头Last-Modified
			this.headers.put("Last-Modified", lastModifyDate);

			// 设置响应状态码200
			setResponseCode(ResponseUtil.RESPONSE_CODE_200);
		} catch (NoSuchFileException e) {
			// 没有本地资源被找到
			log.error("", e);
			// 设置响应状态码404
			setResponseCode(ResponseUtil.RESPONSE_CODE_404);
			// 关闭本地文件通道
			closeLocalFileChannel();
		} catch (IOException e) {
			// 打开资源时出现异常
			log.error("", e);
			// 设置响应状态码500
			setResponseCode(ResponseUtil.RESPONSE_CODE_500);
			// 关闭本地文件通道
			closeLocalFileChannel();
		}
		// debug
		log.info(String.format("Request %s is [%s]", uri, status));
	}

	@Override
	public void setResponseCode(int status) {
		this.status = status;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
		headers.put("Content-Type", this.contentType);
	}

	@Override
	public void setCharsetEncoding(String charsetName) {
		// 获取GBK字符集
		Charset c1 = Charset.forName(charsetName);
		// 获取编码器
		encoder = c1.newEncoder();
	}

	@Override
	public void response() {
		try {
			// 输出响应首行
			writeResponseLine();
			// 输出Header
			writeHeaders();
			// 输出全部cookie
			writeCookies();

			// 再输出一个换行，目的是输出一个空白行，下面就是响应主体了
			newLine();

			// 304
			if (status == ResponseUtil.RESPONSE_CODE_304) {
				// debug
				log.info(String.format("Request handle ok [%s %s %s]",
						req.getRequestURI(), contentType, status));
				return;
			}

			// 输出响应主体
			if (in != null && in.size() > 0) {
				// 输出本地资源
				long size = in.size();
				long pos = 0;
				long count = 0;

				long len = -1;

				while (pos < size) {
					len = size - pos;
					count = len > 31457280 ? 31457280 : len;
					pos += in.transferTo(pos, count, out);
				}
			} else if (html != null) {
				// 输出模板解析的html文档
				write(html);
			} else {
				// 输出动态程序解析后的字符串
				write(content.toString());
			}

			// debug
			log.info(String.format("Request handle ok [%s %s %s]",
					req.getRequestURI(), contentType, status));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			// 关闭本地文件通道
			closeLocalFileChannel();
		}
	}

	/**
	 * 输出一个换行<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:44:46
	 * @throws IOException
	 */
	private void newLine() throws IOException {
		write("\n");
	}

	/**
	 * 输出响应首行<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:45:02
	 * @throws IOException
	 */
	private void writeResponseLine() throws IOException {
		write(ResponseUtil.getResponseLine(this.status));
		newLine();
	}

	/**
	 * 输出响应header<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:45:15
	 * @throws IOException
	 */
	private void writeHeaders() throws IOException {
		Set<Entry<String, String>> entrys = headers.entrySet();
		for (Iterator<Entry<String, String>> i = entrys.iterator(); i.hasNext();) {
			Entry<String, String> entry = i.next();
			String headerContent = entry.getKey() + ": " + entry.getValue();
			write(headerContent);
			newLine();
		}
	}

	/**
	 * 把全部cookie写到响应通道<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月31日 上午11:29:26
	 * @throws IOException
	 */
	private void writeCookies() throws IOException {
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			if (StringUtil.isNullOrEmpty(name)
					|| StringUtil.isNullOrEmpty(value)) {
				log.warn("Cookie name or value is null");
				continue;
			}
			// 构造cookie响应头
			StringBuilder s = new StringBuilder("Set-Cookie: ");
			// cookie名字和值
			s.append(name);
			s.append("=");
			s.append(value);
			s.append("; ");
			// 设置过期时间
			long age = cookie.getAge();
			if (age > -1) {
				long expiresTimeStamp = System.currentTimeMillis() + age;
				s.append("Expires=");
				s.append(sdf.format(new Date(expiresTimeStamp)));
				s.append("; ");
			}
			// 设置path
			String path = cookie.getPath();
			if (!StringUtil.isNullOrEmpty(path)) {
				s.append("Path=");
				s.append(path);
				s.append("; ");
			}
			// 设置domain
			String domain = cookie.getDomain();
			if (!StringUtil.isNullOrEmpty(domain)) {
				s.append("Domain=");
				s.append(domain);
				s.append("; ");
			}
			// http only
			s.append("HttpOnly");
			// 写到响应通道
			write(s.toString());
			newLine();
		}
	}

	/**
	 * 把指定的字符串输出到客户端channel<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:45:29
	 * @param content
	 *            - 字符串
	 * @throws IOException
	 */
	private void write(String content) throws IOException {
		CharBuffer cBuf = CharBuffer.allocate(content.length());
		cBuf.put(content);
		cBuf.flip();
		ByteBuffer bBuf = encoder.encode(cBuf);
		out.write(bBuf);
	}

	/**
	 * 关闭本地资源输入通道
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月29日 下午4:43:30
	 */
	private void closeLocalFileChannel() {
		try {
			// 关闭本地文件通道
			if (in != null) {
				in.close();
				in = null;
			}
		} catch (IOException e) {
		}
	}

	@Override
	@Deprecated
	public SocketChannel getOut() {
		return out;
	}

	@Override
	public void setHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
	}

	@Override
	public void print(String line) {
		content.append(line);
	}

	@Override
	public void println(String line) {
		content.append(line);
		content.append("\n");
	}

	@Override
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	@Override
	public void render(View view) throws IOException {

		if (view == null) {
			return;
		}
		if (StringUtil.isNullOrEmpty(view.getViewName())) {
			return;
		}

		// 获取模板File对象
		File template = new File(viewTemplateDir + "/" + view.getViewName());
		if (!template.exists() || !template.isFile()) {
			throw new FileNotFoundException("Template file not found: "
					+ viewTemplateDir + "/" + view.getViewName());
		}

		// 解析视图，获取文件输入流
		ViewResovler resolver = ViewResovler.getViewResovler();
		html = resolver.resolveView(template, view.getModel());
	}
}
