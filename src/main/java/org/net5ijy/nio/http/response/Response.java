package org.net5ijy.nio.http.response;

import java.io.IOException;

import org.net5ijy.nio.http.response.view.View;

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
	 * 添加一个cookie到响应中<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月31日 上午11:13:17
	 * @param cookie
	 */
	void addCookie(Cookie cookie);

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

	/**
	 * 渲染动态资源的响应视图<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月16日 上午9:03:58
	 * @param view
	 */
	void render(View view) throws IOException;

	/**
	 * 加载本地资源<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月23日 上午8:33:10
	 */
	void initLocalResource();
}
