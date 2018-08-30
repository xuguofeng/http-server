package org.net5ijy.nio.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Test;

public class TestChannel {

	@Test
	public void test1() {

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;

		try {

			fis = new FileInputStream("e:/1.wmv");
			fos = new FileOutputStream("e:/2.wmv");

			in = fis.getChannel();
			out = fos.getChannel();

			ByteBuffer buf = ByteBuffer.allocate(102400000);

			while (in.read(buf) != -1) {
				buf.flip();
				out.write(buf);
				buf.clear();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test2() {

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;

		try {

			fis = new FileInputStream("e:/1.wmv");
			fos = new FileOutputStream("e:/2.wmv");

			in = fis.getChannel();
			out = fos.getChannel();

			long size = in.size();
			long pos = 0;
			long count = 0;

			while (pos < size) {
				count = size - pos > 31457280 ? 31457280 : size - pos;
				pos += out.transferFrom(in, pos, count);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void test3() {

		FileChannel in = null;
		FileChannel out = null;

		try {

			in = FileChannel.open(Paths.get("e:/1.wmv"),
					StandardOpenOption.READ);
			out = FileChannel.open(Paths.get("e:/2.wmv"),
					StandardOpenOption.READ, StandardOpenOption.WRITE,
					StandardOpenOption.CREATE_NEW);

			MappedByteBuffer inBuf = in.map(MapMode.READ_ONLY, 0, in.size());

			MappedByteBuffer outBuf = out.map(MapMode.READ_WRITE, 0, in.size());

			outBuf.put(inBuf);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
