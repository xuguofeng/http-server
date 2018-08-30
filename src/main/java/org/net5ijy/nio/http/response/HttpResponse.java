package org.net5ijy.nio.http.response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.net5ijy.nio.http.config.ContentTypeUtil;
import org.net5ijy.nio.http.config.HttpServerConfig;
import org.net5ijy.nio.http.config.ResponseUtil;
import org.net5ijy.nio.http.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse implements Response {

	private static Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	private CharsetEncoder encoder;

	private String contentType = "text/html;charset=utf-8";

	private int status = 0;

	private Map<String, String> headers = new HashMap<String, String>();

	private FileChannel in;
	private SocketChannel out;

	private StringBuilder content = new StringBuilder();

	// 获取服务器配置
	HttpServerConfig config = HttpServerConfig.getInstance();

	static {
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * 根据指定的客户端SocketChannel创建HttpResponse<br />
	 * <br />
	 * 设置Content-Type、添加响应header<br />
	 * <br />
	 * 
	 * @param sChannel
	 *            - 客户端通道
	 */
	public HttpResponse(SocketChannel sChannel) {
		// 获取GBK字符集
		Charset c1 = Charset.forName(config.getResponseCharset());
		// 获取编码器
		this.encoder = c1.newEncoder();
		// 获取Content-Type
		this.setContentType(ContentTypeUtil
				.getContentType(ContentTypeUtil.HTML));
		this.headers.put("Date", sdf.format(new Date()));
		this.headers.put("Server", "nginx");
		this.headers.put("Connection", "keep-alive");
		// 客户端输出通道
		this.out = sChannel;
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
	 */
	public HttpResponse(Request req, SocketChannel sChannel) {

		this(sChannel);

		// 获取请求资源URI
		String uri = req.getRequestURI();

		// 获取本地输入通道
		this.getLocalFileChannel(uri);

		// 设置Content-Type
		this.setContentType(req.getContentType());

		// 设置静态资源过期响应头
		int expires = config.getExpiresMillis(this.contentType);
		if (expires > 0) {
			long expiresTimeStamp = System.currentTimeMillis() + expires;
			this.headers.put("Expires", sdf.format(new Date(expiresTimeStamp)));
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
			this.in = FileChannel.open(Paths.get(config.getRoot(), uri),
					StandardOpenOption.READ);
			// 设置响应状态码200
			this.setResponseCode(ResponseUtil.RESPONSE_CODE_200);
		} catch (NoSuchFileException e) {
			// 没有本地资源被找到
			log.error("", e);
			// 设置响应状态码404
			this.setResponseCode(ResponseUtil.RESPONSE_CODE_404);
			// 关闭本地文件通道
			this.closeLocalFileChannel();
		} catch (IOException e) {
			// 打开资源时出现异常
			log.error("", e);
			// 设置响应状态码500
			this.setResponseCode(ResponseUtil.RESPONSE_CODE_500);
			// 关闭本地文件通道
			this.closeLocalFileChannel();
		}
		// debug
		if (log.isDebugEnabled()) {
			log.debug(String.format("Request %s is [%s]", uri, status));
		}
	}

	@Override
	public void setResponseCode(int status) {
		this.status = status;
	}

	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
		this.headers.put("Content-Type", this.contentType);
	}

	@Override
	public void setCharsetEncoding(String charsetName) {
		// 获取GBK字符集
		Charset c1 = Charset.forName(charsetName);
		// 获取编码器
		this.encoder = c1.newEncoder();
	}

	@Override
	public void response() {
		try {
			// 输出响应首行
			this.writeResponseLine();
			// 输出Header
			this.writeHeaders();

			// 304
			if (this.status == ResponseUtil.RESPONSE_CODE_304) {
				// debug
				if (log.isDebugEnabled()) {
					log.debug(String.format("Request handle ok [%s %s]",
							contentType, status));
				}
				return;
			}

			// 输出响应主体
			if (in != null && in.size() > 0) {
				// 输出本地资源
				long size = in.size();
				long pos = 0;
				long count = 0;

				while (pos < size) {
					count = size - pos > 31457280 ? 31457280 : size - pos;
					pos += in.transferTo(pos, count, out);
				}
			} else {
				// 输出动态程序解析后的字符串
				this.write(content.toString());
			}

			// debug
			if (log.isDebugEnabled()) {
				log.debug(String.format("Request handle ok [%s %s]",
						contentType, status));
			}
		} catch (IOException e) {
			log.error("", e);
		} finally {
			// 关闭本地文件通道
			this.closeLocalFileChannel();
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
		this.write("\n");
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
		this.write(ResponseUtil.getResponseLine(this.status));
		this.newLine();
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
		Set<Entry<String, String>> entrys = this.headers.entrySet();
		for (Iterator<Entry<String, String>> i = entrys.iterator(); i.hasNext();) {
			Entry<String, String> entry = i.next();
			String headerContent = entry.getKey() + ": " + entry.getValue();
			this.write(headerContent);
			this.newLine();
		}
		this.newLine();// 再输出一个换行，目的是输出一个空白行，下面就是响应主体了
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
		ByteBuffer bBuf = this.encoder.encode(cBuf);
		this.out.write(bBuf);
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
			if (this.in != null) {
				this.in.close();
				this.in = null;
			}
		} catch (IOException e) {
		}
	}

	@Override
	@Deprecated
	public SocketChannel getOut() {
		return this.out;
	}

	@Override
	public void setHeader(String headerName, String headerValue) {
		this.headers.put(headerName, headerValue);
	}

	@Override
	public void print(String line) {
		this.content.append(line);
	}

	@Override
	public void println(String line) {
		this.content.append(line);
		this.content.append("\n");
	}
}
