package org.net5ijy.nio.test;

import java.nio.ByteBuffer;

import org.junit.Test;

public class TestBuffer {

	@Test
	public void testBuffer() {

		// 分配1024长度的字节缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);

		System.out.println("---- allocate ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// put字符串
		String str = "abcde";
		buf.put(str.getBytes());

		System.out.println("---- put ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 开启读模式
		buf.flip();

		System.out.println("---- flip ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 读取
		byte[] dst = new byte[buf.limit()];
		buf.get(dst, 0, dst.length);

		String newStr = new String(dst, 0, dst.length);

		System.out.println("---- get ----");
		System.out.println(newStr);
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 恢复读取前的状态
		buf.rewind();
		System.out.println("---- rewind ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 清空
		buf.clear();
		System.out.println("---- clear ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
	}

	@Test
	public void testMarkAndReser() {

		// 分配1024长度的字节缓冲区
		ByteBuffer buf = ByteBuffer.allocate(1024);

		System.out.println("---- allocate ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// put字符串
		String str = "abcde";
		buf.put(str.getBytes());

		System.out.println("---- put ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 开启读模式
		buf.flip();

		System.out.println("---- flip ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 读取两个字节
		byte[] dst = new byte[buf.limit()];

		buf.get(dst, 0, 2);

		String newStr = new String(dst, 0, dst.length);

		System.out.println("---- get ----");
		System.out.println(newStr);
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		// 标记
		buf.mark();

		buf.get(dst, buf.position(), 2);

		newStr = new String(dst, 2, 2);

		System.out.println("---- get ----");
		System.out.println(newStr);
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());

		//
		buf.reset();
		System.out.println("---- reset ----");
		System.out.println(buf.position());
		System.out.println(buf.limit());
		System.out.println(buf.capacity());
	}

	@Test
	public void testDirectBuffer() {
		ByteBuffer buf = ByteBuffer.allocateDirect(1024);
		System.out.println(buf.isDirect());
	}
}
