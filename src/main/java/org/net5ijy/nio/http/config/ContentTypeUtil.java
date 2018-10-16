package org.net5ijy.nio.http.config;

import java.util.HashMap;
import java.util.Map;

import org.net5ijy.util.StringUtil;

@SuppressWarnings("deprecation")
public class ContentTypeUtil {

	/**
	 * 封装服务器支持的Content-Type<br />
	 * <br />
	 * key为请求资源uri的后缀<br />
	 * <br />
	 * value为对应的Content-Type<br />
	 * <br />
	 */
	private static Map<String, String> contentTypes = new HashMap<String, String>();

	public static final String HTML = "html";
	public static final String CSS = "css";
	public static final String JS = "js";
	public static final String JPG = "jpg";
	public static final String JPEG = "jpeg";
	public static final String PNG = "png";
	public static final String GIF = "gif";
	public static final String ICO = "ico";
	public static final String TXT = "txt";

	private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

	static {
		contentTypes.put("html", "text/html;charset=" + ResponseUtil.CHARSET);
		contentTypes.put("css", "text/css");
		contentTypes.put("js", "application/javascript");
		contentTypes.put("jpg", "image/jpeg");
		contentTypes.put("jpeg", "image/jpeg");
		contentTypes.put("png", "image/png");
		contentTypes.put("gif", "image/gif");
		contentTypes.put("ico", "image/x-icon");
		contentTypes.put("txt", "text/plain");
	}

	/**
	 * 根据指定的请求uri后缀获取对应的Content-Type<br />
	 * <br />
	 * 如果参数suffix为空，直接返回text/html<br />
	 * <br />
	 * 如果从contentTypes获取不到，返回application/octet-stream<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午4:03:09
	 * @param suffix
	 *            - 请求uri后缀
	 * @return String - Content-Type
	 */
	public static String getContentType(String suffix) {
		// 如果资源uri后缀为空，直接返回text/html
		if (StringUtil.isNullOrEmpty(suffix)) {
			return contentTypes.get(HTML);
		}
		// 根据后缀从contentTypes获取
		String contentType = contentTypes.get(suffix);
		// 如果没有获取到，返回application/octet-stream
		if (contentType == null) {
			return APPLICATION_OCTET_STREAM;
		}
		return contentType;
	}
}
