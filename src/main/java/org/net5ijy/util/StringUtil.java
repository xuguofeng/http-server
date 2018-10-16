package org.net5ijy.util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串工具类
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年7月3日 上午11:48:36
 */
public class StringUtil {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

	/**
	 * 首字母转大写
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月3日 上午11:49:02
	 * @param str
	 * @return
	 */
	public static String captureName(String str) {
		if (isNullOrEmpty(str)) {
			return str;
		}
		char[] cs = str.toCharArray();
		if (cs[0] > 'z' || cs[0] < 'a') {
			return str;
		}
		cs[0] -= 32;
		return String.valueOf(cs);
	}

	/**
	 * 验证字符串是否为null或长度为0，此方法不会对字符串进行trim操作
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年5月19日 下午1:03:03
	 * @param str
	 * @return true - 为空<br />
	 *         false - 不为空
	 */
	public static boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	/**
	 * 获取UTF-8编码方式的字符串
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年6月28日 下午12:56:45
	 * @param text
	 * @return
	 */
	public static String getUtf8Text(String text) {
		try {
			return new String(text.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return text;
	}

	/**
	 * 把字符串转为Integer
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月4日 上午10:44:06
	 * @param str
	 *            - 字符串
	 * @param defaultVal
	 *            - 默认值，在数值转换失败时使用
	 * @return
	 */
	public static Integer getInteger(String str, Integer defaultVal) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return defaultVal;
		}
	}

	/**
	 * 使用指定的格式格式化时间
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月5日 上午8:28:35
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDatetime(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * 使用yyyy-MM-dd HH:mm:ss格式格式化时间
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月5日 上午8:28:35
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatDatetime(Date date) {
		return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT).format(date);
	}

	/**
	 * 格式化日期，yyyy-MM-dd
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月5日 上午8:29:28
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return formatDatetime(date, DEFAULT_DATE_FORMAT);
	}

	/**
	 * 格式化时间，HH:mm:ss
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年7月5日 上午8:30:12
	 * @param date
	 * @return
	 */
	public static String formatTime(Date date) {
		return formatDatetime(date, DEFAULT_TIME_FORMAT);
	}

	/**
	 * 根据指定的格式把字符串转为时间
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月13日 上午8:59:40
	 * @param formatDate
	 * @param format
	 * @return
	 */
	public static Date parseDateTime(String formatDate, String format) {
		try {
			return new SimpleDateFormat(format).parse(formatDate);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 根据默认格式把字符串转为时间：yyyy-MM-dd HH:mm:ss
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月13日 上午9:02:37
	 * @param formatDate
	 * @return
	 */
	public static Date parseDateTime(String formatDate) {
		return parseDateTime(formatDate, DEFAULT_DATETIME_FORMAT);
	}

	/**
	 * 根据默认格式把字符串转为日期：yyyy-MM-dd
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年10月13日 上午9:02:37
	 * @param formatDate
	 * @return
	 */
	public static Date parseDate(String formatDate) {
		return parseDateTime(formatDate, DEFAULT_DATE_FORMAT);
	}
}
