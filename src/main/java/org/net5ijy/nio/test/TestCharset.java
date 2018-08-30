package org.net5ijy.nio.test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

public class TestCharset {

	/**
	 * 获取全部可以使用的Charset
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月24日 上午9:35:55
	 */
	@Test
	public void test1() {

		// 获取全部可以使用的Charset
		Map<String, Charset> charsets = Charset.availableCharsets();
		Set<Entry<String, Charset>> entrys = charsets.entrySet();
		// 遍历
		for (Entry<String, Charset> entry : entrys) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}

	/**
	 * 测试编码和解码
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月24日 上午9:36:13
	 * @throws IOException
	 */
	@Test
	public void test2() throws IOException {

		// 获取gbk字符集
		Charset c1 = Charset.forName("gbk");

		// 获取编码器
		CharsetEncoder encoder = c1.newEncoder();

		// 中文字符串
		String name = "你好，我是测试账号";

		// 分配字符缓冲区
		CharBuffer cBuf1 = CharBuffer.allocate(name.length());
		// 把字符串存储到字符缓冲区
		cBuf1.put(name);

		cBuf1.flip();

		// 编码
		ByteBuffer bBuf1 = encoder.encode(cBuf1);

		// 获取编码后的字节数组
		byte[] b = new byte[bBuf1.limit()];
		bBuf1.get(b, 0, bBuf1.limit());

		System.out.println(b.length);
		System.out.println(Arrays.toString(b));

		// 获取解码器
		CharsetDecoder decoder = c1.newDecoder();
		// 分配字节缓冲区
		ByteBuffer bBuf2 = ByteBuffer.allocate(b.length);

		bBuf2.put(b);

		bBuf2.flip();

		// 解码
		CharBuffer cBuf2 = decoder.decode(bBuf2);

		System.out.println(cBuf2.toString());
	}
}
