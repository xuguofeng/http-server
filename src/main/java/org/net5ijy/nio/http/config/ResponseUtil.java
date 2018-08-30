package org.net5ijy.nio.http.config;

public class ResponseUtil {

	/**
	 * 200状态码
	 */
	public static final int RESPONSE_CODE_200 = 200;
	/**
	 * 304状态码
	 */
	public static final int RESPONSE_CODE_304 = 304;
	/**
	 * 404状态码
	 */
	public static final int RESPONSE_CODE_404 = 404;
	/**
	 * 500状态码
	 */
	public static final int RESPONSE_CODE_500 = 500;

	/**
	 * HTTP/1.1 200 OK
	 */
	public static final String RESPONSE_LINE_200 = "HTTP/1.1 200 OK";
	/**
	 * HTTP/1.1 304 Not Modified
	 */
	public static final String RESPONSE_LINE_304 = "HTTP/1.1 304 Not Modified";
	/**
	 * HTTP/1.1 404 Not Found
	 */
	public static final String RESPONSE_LINE_404 = "HTTP/1.1 404 Not Found";
	/**
	 * HTTP/1.1 500 Internal Server Error
	 */
	public static final String RESPONSE_LINE_500 = "HTTP/1.1 500 Internal Server Error";

	/**
	 * 服务部署跟目录: 默认是工作目录下面的WebContent
	 */
	@Deprecated
	public static final String WEB_ROOT = "WebContent";

	/**
	 * 404默认页面位置: WebContent/404.html
	 */
	@Deprecated
	public static final String RESPONSE_PAGE_404 = WEB_ROOT + "/404.html";

	/**
	 * 默认编码解码字符集
	 */
	@Deprecated
	public static final String CHARSET = "utf-8";

	/**
	 * 根据指定的响应状态码获取响应首行<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午4:06:36
	 * @param status
	 *            - 响应状态码
	 * @return
	 */
	public static String getResponseLine(int status) {
		switch (status) {
		case RESPONSE_CODE_200:
			return RESPONSE_LINE_200;
		case RESPONSE_CODE_304:
			return RESPONSE_LINE_304;
		case RESPONSE_CODE_404:
			return RESPONSE_LINE_404;
		case RESPONSE_CODE_500:
			return RESPONSE_LINE_500;
		}
		return RESPONSE_LINE_200;
	}
}
