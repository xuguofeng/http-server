package org.net5ijy.nio.http.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.nio.http.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务器配置类<br />
 * <br />
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年8月30日 上午9:20:20
 */
public class HttpServerConfig {

	private static Logger log = LoggerFactory.getLogger(HttpServerConfig.class);

	private static final String DEFAULT_PORT = "80";

	private static final String DEFAULT_ROOT = "WebContent";

	private static final String DEFAULT_PAGE_404 = "WebContent/404.html";

	private static final String DEFAULT_CHARSET = "utf-8";

	/**
	 * 服务器监听端口
	 */
	private int serverPort;
	/**
	 * 服务部署根目录
	 */
	private String root;
	/**
	 * 404页面
	 */
	private String page404;
	/**
	 * 编码
	 */
	private String requestCharset;
	private String responseCharset;

	/**
	 * 静态资源过期配置信息<br />
	 * <br />
	 * content-type : 过期毫秒<br />
	 * <br />
	 */
	private Map<String, Integer> expires = new HashMap<String, Integer>();

	/**
	 * URL映射关系信息<br />
	 * <br />
	 * URI : Servlet-Class<br />
	 * <br />
	 */
	private Map<String, Class<? extends Servlet>> urlMappings = new HashMap<String, Class<? extends Servlet>>();

	private static HttpServerConfig config = new HttpServerConfig();

	/**
	 * 初始化服务器配置信息
	 */
	private HttpServerConfig() {

		Properties p = new Properties();

		try {
			// 加载server.properties主配置文件
			p.load(HttpServerConfig.class.getClassLoader().getResourceAsStream(
					"server.properties"));

			this.init(p);

			// 解析配置
			this.initConfig(p);

		} catch (IOException e) {
			log.error("Load server.properties error: ", e);
		}
	}

	/**
	 * 初始化操作<br />
	 * <br />
	 * 部署目录、404页面、字符集、监听端口<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午11:34:50
	 * @param properties
	 */
	private void init(Properties properties) {
		this.root = properties.getProperty("server.root", DEFAULT_ROOT);
		this.page404 = properties.getProperty("server.404.page",
				DEFAULT_PAGE_404);
		this.requestCharset = properties.getProperty("request.charset",
				DEFAULT_CHARSET);
		this.responseCharset = properties.getProperty("response.charset",
				DEFAULT_CHARSET);
		this.serverPort = Integer.valueOf(properties.getProperty("server.port",
				DEFAULT_PORT));
		// debug
		if (log.isDebugEnabled()) {
			log.debug(String
					.format("Initialize server ok: [ port=%s, request.charset=%s, response.charset=%s, root=%s, page404=%s ]",
							this.serverPort, this.requestCharset,
							this.responseCharset, this.root, this.page404));
		}
	}

	/**
	 * 设置静态资源过期配置<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午9:22:28
	 * @param properties
	 */
	private void addExpiresMillis(String key, String val) {
		String suffix = key.substring(key.indexOf(".") + 1);
		String contentType = ContentTypeUtil.getContentType(suffix);
		if (!StringUtil.isNullOrEmpty(contentType)) {
			this.expires.put(contentType, Integer.valueOf(val));
			// debug
			if (log.isDebugEnabled()) {
				log.debug(String.format("Initialize expires ok [ %s: %s ]",
						contentType, val));
			}
		}
	}

	/**
	 * 添加URL映射<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午10:49:40
	 * @param key
	 * @param val
	 */
	private void addUrlMapping(String key, String val) {
		String suffix = key.substring(key.indexOf(".") + 1);
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Servlet> clazz = (Class<? extends Servlet>) Class
					.forName(val);
			this.urlMappings.put("/" + suffix, clazz);
			// debug
			if (log.isDebugEnabled()) {
				log.debug(String.format("Initialize url mapping ok [ %s: %s ]",
						suffix, val));
			}
		} catch (ClassNotFoundException e) {
			log.error("", e);
		}
	}

	/**
	 * 解析配置文件，进行服务器配置<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午9:28:15
	 * @param properties
	 */
	private void initConfig(Properties properties) {
		Set<Object> keys = properties.keySet();
		for (Object k : keys) {
			String key = k.toString();
			if (key.startsWith("expires.")) {
				this.addExpiresMillis(key, properties.getProperty(key, "0"));
			} else if (key.startsWith("servlet.")) {
				this.addUrlMapping(key, properties.getProperty(key));
			}
		}
	}

	/**
	 * 获取服务器配置单实例对象<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午9:22:52
	 * @return
	 */
	public static HttpServerConfig getInstance() {
		return config;
	}

	/**
	 * 获取服务器监听端口<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午9:23:06
	 * @return - 端口
	 */
	public int getServerPort() {
		return this.serverPort;
	}

	/**
	 * 根据Content-Type获取对应类型静态资源的过期毫秒值<br />
	 * <br />
	 * 如果未配置的类型，返回 0 即不进行过期设置<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午9:23:23
	 * @param contentType
	 *            - 资源的Content-Type
	 * @return - 毫秒值
	 */
	public int getExpiresMillis(String contentType) {
		Integer i = this.expires.get(contentType);
		return i == null ? 0 : i.intValue();
	}

	/**
	 * 根据指定的URI获取处理这个请求的Servlet类<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午10:50:03
	 * @param uri
	 *            - 请求uri
	 * @return
	 */
	public Class<? extends Servlet> getServlet(String uri) {
		return this.urlMappings.get(uri);
	}

	public String getRoot() {
		return this.root;
	}

	public String get404Page() {
		return this.page404;
	}

	public String getRequestCharset() {
		return this.requestCharset;
	}

	public String getResponseCharset() {
		return this.responseCharset;
	}
}
