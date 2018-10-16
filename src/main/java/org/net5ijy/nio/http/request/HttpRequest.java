package org.net5ijy.nio.http.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.net5ijy.nio.http.config.ContentTypeUtil;
import org.net5ijy.nio.http.config.HttpServerConfig;
import org.net5ijy.nio.http.config.ResponseUtil;
import org.net5ijy.nio.http.response.Cookie;
import org.net5ijy.nio.http.session.Session;
import org.net5ijy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequest implements Request {

	private static Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private String method;

	private String requestURI;

	private String protocol = "HTTP/1.1";

	private String host = "localhost";
	private int port = 80;

	private String contentType = "";

	private Map<String, String> parameters = new HashMap<String, String>();
	private Map<String, String> headers = new HashMap<String, String>();
	private List<Cookie> cookies = new ArrayList<Cookie>();

	private Session session = null;

	// 获取服务器配置
	HttpServerConfig config = HttpServerConfig.getInstance();

	/**
	 * 根据客户端传来的请求信息初始化Request<br />
	 * <br />
	 * 1. 从请求首行截取method和资源uri<br />
	 * <br />
	 * 2. 解析请求头<br />
	 * <br />
	 * 3. 解析请求参数<br />
	 * <br />
	 * 可以是请求uri后面的参数，格式arg1=val1&arg2=val2<br />
	 * <br />
	 * 可以是post提交的参数，格式arg1=val1&arg2=val2<br />
	 * <br />
	 * 可以是post方式提交的json格式的数据<br />
	 * <br />
	 * 
	 * @param body
	 *            - 客户端的请求信息字符串
	 */
	public HttpRequest(String body) {

		// 所在平台的行分隔符
		String lineSeparator = System.getProperty("line.separator");

		// 获取请求行
		String requestLine = body.substring(0, body.indexOf(lineSeparator));
		// 获取请求方法和资源uri
		String[] requestLines = requestLine.split("\\s+");
		this.method = requestLines[0];
		this.protocol = requestLines[2];

		// 解析请求uri
		String[] uri = requestLines[1].split("\\?");
		this.requestURI = uri[0];
		// 解析uri后面跟的请求参数
		if (uri.length > 1) {
			this.parameters.putAll(resolveRequestArgs(uri[1]));
		}

		// 截取请求头和请求主体
		body = body.substring(body.indexOf(lineSeparator) + 2);

		// 获取请求头
		int num = 0;
		String[] headerAndParameter = body.split(lineSeparator);
		for (; num < headerAndParameter.length; num++) {
			String headerLine = headerAndParameter[num];
			// 遍历到请求主体上面的空白行就停止
			if (StringUtil.isNullOrEmpty(headerLine)) {
				break;
			}
			// 获取第一个“:”的下标
			int indexOfMaohao = headerLine.indexOf(":");
			if (indexOfMaohao == -1) {
				continue;
			}
			String headerName = headerLine.substring(0, indexOfMaohao).trim();
			String headerValue = headerLine.substring(indexOfMaohao + 1).trim();
			this.headers.put(headerName, headerValue);
			// debug
			if (log.isDebugEnabled()) {
				log.debug(String.format("Request %s header %s = %s",
						this.requestURI, headerName, headerValue));
			}
		}

		// 获取请求cookie
		String cookieHeader = headers.get("Cookie");
		if (!StringUtil.isNullOrEmpty(cookieHeader)) {
			String[] cookiesArray = cookieHeader.split(";\\s*");
			for (int i = 0; i < cookiesArray.length; i++) {
				String cookieStr = cookiesArray[i];
				if (!StringUtil.isNullOrEmpty(cookieStr)) {
					String[] cookieArray = cookieStr.split("=");
					if (cookieArray.length == 2) {
						Cookie c = new Cookie(cookieArray[0], cookieArray[1],
								-1);
						this.cookies.add(c);
						if (log.isDebugEnabled()) {
							log.debug("Recieve request cookie " + c);
						}
					}
				}
			}
		}

		// 获取请求参数
		num++;
		StringBuilder builder = new StringBuilder();
		for (; num < headerAndParameter.length; num++) {
			builder.append(headerAndParameter[num]);
		}
		String requestArgs = builder.toString();

		if (!StringUtil.isNullOrEmpty(requestArgs)) {
			if (requestArgs.indexOf("{") > -1) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					@SuppressWarnings("unchecked")
					Map<String, String> map = mapper.readValue(requestArgs,
							Map.class);
					this.parameters.putAll(map);
				} catch (Exception e) {
					log.error("", e);
				}
			} else if (requestArgs.indexOf("&") > -1) {
				this.parameters.putAll(resolveRequestArgs(requestArgs));
			}
		}

		// 获取host、port和content-type
		this.initHostAndPort();
		this.initContentType();

		// debug
		if (log.isDebugEnabled()) {
			log.debug(String.format("Request method is %s", method));
			log.debug(String.format("Request uri is %s", requestURI));
			log.debug(String.format("Request args is %s", parameters));
		}
	}

	/**
	 * 解析arg1=val1&arg2=val2格式的请求参数<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月29日 上午10:48:27
	 * @param args
	 *            - 请求参数字符串
	 * @return
	 */
	private Map<String, String> resolveRequestArgs(String args) {
		Map<String, String> map = new HashMap<String, String>();
		if (!StringUtil.isNullOrEmpty(args)) {
			String[] argss = args.split("&+");
			for (int i = 0; i < argss.length; i++) {
				String arg = argss[i];
				String[] nameAndValue = arg.split("\\s*=\\s*");
				if (nameAndValue.length == 2) {
					map.put(nameAndValue[0], nameAndValue[1]);
				} else if (nameAndValue.length == 1) {
					map.put(nameAndValue[0], "");
				}
			}
		}
		return map;
	}

	/**
	 * 从Host请求头中获取host和port<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年9月10日 上午8:13:36
	 */
	private void initHostAndPort() {
		// 获取Hostqing请求头
		String hostAndPort = this.headers.get("Host");
		if (!StringUtil.isNullOrEmpty(hostAndPort)) {
			this.host = hostAndPort.split(":")[0];
		}
		if (!StringUtil.isNullOrEmpty(hostAndPort)
				&& hostAndPort.indexOf(":") > -1) {
			this.port = Integer.parseInt(hostAndPort.split(":")[1]);
		}
	}

	/**
	 * 根据请求uri后缀获取Content-Type<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年9月10日 上午8:15:25
	 */
	private void initContentType() {
		if (this.requestURI.indexOf(".") == -1) {
			this.contentType = ContentTypeUtil
					.getContentType(ContentTypeUtil.HTML);
		} else {
			String suffix = this.requestURI.substring(this.requestURI
					.lastIndexOf(".") + 1);
			this.contentType = ContentTypeUtil.getContentType(suffix);
		}
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getRequestURI() {
		return this.requestURI;
	}

	@Override
	public String getProtocol() {
		return this.protocol;
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	@Override
	public String getParameter(String paramaterName) {
		return this.parameters.get(paramaterName);
	}

	@Override
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	@Override
	public String getHeader(String headerName) {
		return this.headers.get(headerName);
	}

	@Override
	public List<Cookie> getCookies() {
		return this.cookies;
	}

	@Override
	public Session getSession() {
		if (session == null) {
			session = config.getSessionManager().getSession(getSessionId());
		}
		session.refreshInactiveTime();
		log.info(String.format("Now is %s, Session %s expires at %s",
				System.currentTimeMillis(), session.getId(),
				session.getInactiveTime()));
		return session;
	}

	private String getSessionId() {
		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			if (ResponseUtil.SESSION_ID_KEY.equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
