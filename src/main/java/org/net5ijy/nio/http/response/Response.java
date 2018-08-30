package org.net5ijy.nio.http.response;

import java.nio.channels.SocketChannel;

/**
 * http响应
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年8月28日 下午3:27:15
 */
public interface Response {

	/**
	 * 设置http响应状态码<br />
	 * <br />
	 * 在 {@link org.net5ijy.nio.http.config.ResponseUtil} 中有 RESPONSE_CODE_200 和
	 * RESPONSE_CODE_404<br />
	 * <br />
	 * 方法内部会根据传入的响应状态码，调用
	 * {@link org.net5ijy.nio.http.util.ResponseUtil.getResponseLine} 生成不同的响应首行<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:27:31
	 * @param status
	 *            - 响应码
	 */
	void setResponseCode(int status);

	/**
	 * 设置http响应的Content-Type<br />
	 * <br />
	 * 常见的静态资源Content-Type在 {@link org.net5ijy.nio.http.config.ContentTypeUtil}
	 * 中有定义<br />
	 * <br />
	 * 可以使用 {@link org.net5ijy.nio.http.util.ContentTypeUtil.getContentType}
	 * 根据请求资源后缀获取相应的Content-Type<br />
	 * <br />
	 * 此方法内部会把传入的Content-Type添加到响应header里面<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:33:00
	 * @param contentType
	 *            - 响应的Content-Type
	 */
	void setContentType(String contentType);

	/**
	 * 设置header<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:36:57
	 * @param headerName
	 *            - header的key
	 * @param headerValue
	 *            - header的值
	 */
	void setHeader(String headerName, String headerValue);

	/**
	 * 设置响应编码字符集<br />
	 * <br />
	 * 如果不显示的调用此方法设置响应字符集，默认使用 {@link org.net5ijy.nio.http.config.ResponseUtil}
	 * 中定义的 CHARSET<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:37:32
	 * @param charsetName
	 *            - 要设置的字符集名字
	 */
	void setCharsetEncoding(String charsetName);

	/**
	 * 响应<br />
	 * <br />
	 * 输出响应首行、响应header和响应主体<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:39:38
	 */
	void response();

	/**
	 * 获取当前请求所对应的客户端socket通道<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午3:40:28
	 * @return SocketChannel - 当前请求所对应的客户端socket通道
	 */
	@Deprecated
	SocketChannel getOut();

	/**
	 * 把指定的字符串写入响应缓冲区<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月29日 下午4:36:30
	 * @param line
	 *            - 需要写入到缓冲区的字符串
	 */
	void print(String line);

	/**
	 * 把指定的字符串写入响应缓冲区，末尾有换行符<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月29日 下午4:36:30
	 * @param line
	 *            - 需要写入到缓冲区的字符串
	 */
	void println(String line);
}
