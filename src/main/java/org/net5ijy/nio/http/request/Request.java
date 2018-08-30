package org.net5ijy.nio.http.request;

import java.util.Map;

/**
 * 封装http请求
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年8月28日 下午3:49:31
 */
public interface Request {

	/**
	 * 获取请求方法<br />
	 * <br />
	 * 从请求首行截取<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:49:43
	 * @return String - 请求方法：GET | POST
	 */
	String getMethod();

	/**
	 * 获取请求资源uri<br />
	 * <br />
	 * 从请求首行截取<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:50:53
	 * @return String - 请求资源uri
	 */
	String getRequestURI();

	/**
	 * 获取客户端请求的协议版本<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月29日 上午9:41:20
	 * @return - 协议版本
	 */
	String getProtocol();

	/**
	 * 获取请求的主机名<br />
	 * <br />
	 * 从Host请求头截取<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:51:20
	 * @return String - 请求的主机名
	 */
	String getHost();

	/**
	 * 获取请求的端口<br />
	 * <br />
	 * 从Host请求头截取<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:52:30
	 * @return int - 请求的端口
	 */
	int getPort();

	/**
	 * 获取请求资源的Content-Type<br />
	 * <br />
	 * 内部根据uri后缀调用
	 * {@link org.net5ijy.nio.http.util.ContentTypeUtil.getContentType} 获取<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:54:06
	 * @return String - 请求资源的Content-Type
	 */
	String getContentType();

	/**
	 * 获取全部请求参数<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:56:06
	 * @return - 全部请求参数
	 */
	Map<String, String> getParameters();

	/**
	 * 获取指定的请求参数的值<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:56:30
	 * @param paramaterName
	 *            - 请求参数名
	 * @return String - 请求参数值
	 */
	String getParameter(String paramaterName);

	/**
	 * 获取全部请求头<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:57:13
	 * @return - 全部请求头
	 */
	Map<String, String> getHeaders();

	/**
	 * 获取指定的请求头的值<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:57:33
	 * @param headerName
	 *            - 请求头的key
	 * @return String - 请求头的值
	 */
	String getHeader(String headerName);
}
