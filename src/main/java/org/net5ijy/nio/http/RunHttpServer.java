package org.net5ijy.nio.http;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http服务器启动类
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年8月28日 下午4:13:53
 */
public class RunHttpServer {

	private static Logger log = LoggerFactory.getLogger(RunHttpServer.class);

	public static void main(String[] args) {
		try {
			new HttpServer().startServer();
		} catch (IOException e) {
			log.error("Start http server fail: ", e);
			System.exit(1);
		}
	}
}
