package org.net5ijy.nio.http.config;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.net5ijy.nio.http.ant.AntPathMatcher;
import org.net5ijy.nio.http.ant.PathMatcher;
import org.net5ijy.nio.http.filter.Filter;
import org.net5ijy.nio.http.filter.FilterChain;
import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.nio.http.session.MemorySessionManager;
import org.net5ijy.nio.http.session.SessionManager;
import org.net5ijy.util.StringUtil;
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

	private static final String DEFAULT_SESSION_MANAGER = "org.net5ijy.nio.http.session.MemorySessionManager";

	private static final String DEFAULT_SESSION_TIMEOUT = "1800";

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
	private Map<String, Servlet> urlMappings = new HashMap<String, Servlet>();

	/**
	 * URL映射关系信息<br />
	 * <br />
	 * URI : Filter-Class<br />
	 * <br />
	 */
	private Map<String, Filter> urlFilterMappings = new HashMap<String, Filter>();

	/**
	 * URL映射关系信息<br />
	 * <br />
	 * URI -&gt; FilterChain<br />
	 * <br />
	 */
	private Map<String, FilterChain> urlChainMappings = new HashMap<String, FilterChain>();

	private SessionManager sessionManager = null;

	private String templateDir;

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

			// 配置SessionManager
			this.initSessionManager(p);

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

		// 获取动态资源模板配置
		String templateDir = properties.getProperty("response.template.dir",
				ResponseUtil.DEFAULT_TEMPLATE_DIR);
		if (templateDir.startsWith("classpath:")) {
			templateDir = templateDir.replaceFirst("classpath.", "");
			this.templateDir = this.getClass().getResource("/" + templateDir)
					.getPath();
		} else {
			this.templateDir = templateDir;
		}

		// debug
		if (log.isDebugEnabled()) {
			log.debug("Initialize template dir: " + this.templateDir);
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
	 * 添加URL——Servlet映射<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午10:49:40
	 * @param key
	 * @param val
	 */
	private Servlet addUrlMapping(String url, String key, String val) {
		try {
			Servlet servlet = (Servlet) Class.forName(val).newInstance();
			urlMappings.put("/" + url, servlet);
			// debug
			if (log.isDebugEnabled()) {
				log.debug(String.format("Initialize url mapping ok [ %s: %s ]",
						url, val));
			}
			return servlet;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 添加URL——Filter映射<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月24日 下午3:11:04
	 * @param key
	 * @param property
	 */
	@SuppressWarnings("unchecked")
	private void addUrlFilterMapping(String key, String property) {
		try {
			Class<Filter> filterClass = (Class<Filter>) Class.forName(property);
			urlFilterMappings.put(key, filterClass.newInstance());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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
				addExpiresMillis(key, properties.getProperty(key, "0"));
			} else if (key.startsWith("servlet.")) {
				String url = key.replaceFirst("servlet.", "");
				addUrlMapping(url, key, properties.getProperty(key));
			} else if (key.startsWith("filter.")) {
				addUrlFilterMapping(key, properties.getProperty(key));
			}
		}
	}

	/**
	 * 配置SessionManager<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月9日 上午10:09:29
	 * @param p
	 */
	private void initSessionManager(Properties p) {
		String managerClassName = p.getProperty("server.session.manager",
				DEFAULT_SESSION_MANAGER);
		int timeout = Integer.valueOf(p.getProperty("server.session.timeout",
				DEFAULT_SESSION_TIMEOUT));
		try {
			Class<?> managerClass = Class.forName(managerClassName);
			sessionManager = (SessionManager) managerClass.newInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sessionManager = new MemorySessionManager();
		}
		sessionManager.setSessionTimeSeconds(timeout);
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
	 * 根据指定的URI获取处理这个请求的Servlet对象<br />
	 * <br />
	 * 
	 * 不再使用，以后使用getFilterChain(requestURI)获取处理请求的过滤器链<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午10:50:03
	 * @param uri
	 *            - 请求uri
	 * @return
	 */
	@Deprecated
	public Servlet getServlet(String uri) {
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

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public String getTemplateDir() {
		return this.templateDir;
	}

	/**
	 * 根据指定的URI获取处理这个请求的过滤器执行链<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月30日 上午10:50:03
	 * @param uri
	 *            - 请求uri
	 * @return
	 */
	public FilterChain getFilterChain(String requestURI) {

		PathMatcher matcher = new AntPathMatcher();

		// 获取处理这个请求的执行链
		FilterChain chain = this.urlChainMappings.get(requestURI);

		if (chain == null) {

			// 获取处理这个请求的Servlet
			Servlet servlet = null;

			Set<String> urlMappingKeys = this.urlMappings.keySet();
			for (String urlMappingKey : urlMappingKeys) {
				if (matcher.match(urlMappingKey, requestURI)) {
					servlet = urlMappings.get(urlMappingKey);
					break;
				}
			}

			if (servlet == null) {
				return null;
			}

			// 匹配全局filter配置除了ant url之外的前缀
			String filterRegex = "filter\\.\\d+\\.";

			// 获取可以拦截这个请求的全部Filter
			TreeSet<String> xx = new TreeSet<String>(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o2.compareTo(o1);
				}
			});

			Set<String> urlFilterKeys = this.urlFilterMappings.keySet();

			for (String k : urlFilterKeys) {
				String antUrl = "/" + k.replaceFirst(filterRegex, "");
				if (matcher.match(antUrl, requestURI)) {
					xx.add(k);
				}
			}

			// 构建执行链
			if (!xx.isEmpty()) {
				FilterChain nextChain = new FilterChain(null, null, servlet);
				for (String k : xx) {
					try {
						Filter filter = this.urlFilterMappings.get(k);
						chain = new FilterChain(filter, nextChain, servlet);
						nextChain = chain;
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			} else {
				chain = new FilterChain(null, null, servlet);
			}
			this.urlChainMappings.put(requestURI, chain);
		}

		// 返回执行链
		return chain;
	}
}
