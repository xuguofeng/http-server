package org.net5ijy.nio.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.net5ijy.nio.http.config.HttpServerConfig;
import org.net5ijy.nio.http.config.ResponseUtil;
import org.net5ijy.nio.http.request.HttpRequest;
import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Cookie;
import org.net5ijy.nio.http.response.HttpResponse;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.nio.http.session.Session;
import org.net5ijy.nio.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http服务器
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年8月28日 下午4:13:41
 */
public class HttpServer {

	private static Logger log = LoggerFactory.getLogger(HttpServer.class);

	private static Map<Integer, SocketChannel> socketChannels = new HashMap<Integer, SocketChannel>();

	private Selector selector;

	private ExecutorService tp = Executors.newCachedThreadPool();

	/**
	 * 服务器监听端口<br />
	 * <br />
	 * 以后使用{@link org.net5ijy.nio.http.config.HttpServerConfig}
	 */
	@Deprecated
	public static final int SERVER_PORT = 8081;

	// 获取服务器配置
	HttpServerConfig config = HttpServerConfig.getInstance();

	/**
	 * 启动服务器<br />
	 * <br />
	 * 在 SERVER_PORT 监听<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午4:10:38
	 * @throws IOException
	 */
	public void startServer() throws IOException {

		selector = Selector.open();

		// 打开服务端socket通道
		ServerSocketChannel ssc = ServerSocketChannel.open();
		// 设置非阻塞
		ssc.configureBlocking(false);
		// 绑定本地端口
		ssc.bind(new InetSocketAddress(config.getServerPort()));
		// 把通道注册到Selector
		ssc.register(selector, SelectionKey.OP_ACCEPT);

		// debug
		log.info(String.format("Start http server, listen at: %s",
				config.getServerPort()));

		while (true) {

			int s = selector.select();
			// 如果没有就绪的通道直接跳过
			if (s <= 0) {
				continue;
			}
			// 获取已经就绪的通道的SelectionKey的集合
			Iterator<SelectionKey> i = selector.selectedKeys().iterator();

			while (i.hasNext()) {

				// 获取当前遍历到的SelectionKey
				SelectionKey sk = i.next();

				// 可连接状态
				try {
					if (sk.isValid() && sk.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) sk
								.channel();
						SocketChannel clientChannel;
						try {
							// 获取客户端channel
							clientChannel = server.accept();
							// 设置非阻塞
							clientChannel.configureBlocking(false);
							// 把通道注册到Selector
							clientChannel.register(selector,
									SelectionKey.OP_READ);
							// debug
							log.info(String.format(
									"Accepted connetion from %s:%s",
									clientChannel.socket().getInetAddress()
											.getHostAddress(), clientChannel
											.socket().getPort()));
						} catch (Exception e) {
							// debug
							log.error("Failed to accept new client: ", e);
						}
					} else if (sk.isValid() && sk.isReadable()) {// 可读取状态
						// 获取通道
						SocketChannel sChannel = (SocketChannel) sk.channel();
						if (socketChannels.get(sChannel.hashCode()) == null) {
							socketChannels.put(sChannel.hashCode(), sChannel);
							tp.execute(new RequestHandler(sk));
							// debug
							if (log.isDebugEnabled()) {
								log.debug(String.format(
										"Handle request from %s:%s", sChannel
												.socket().getInetAddress()
												.getHostAddress(), sChannel
												.socket().getPort()));
							}
						}
					}
				} catch (Exception e) {
					sk.cancel();
					sk = null;
					// debug
					log.error(e.getMessage(), e);
				}
				i.remove();
			}
		}
	}

	/**
	 * 负责处理请求的线程<br />
	 * <br />
	 * 
	 * @author 创建人：xuguofeng
	 * @version 创建于：2018年8月28日 下午4:13:13
	 */
	public class RequestHandler implements Runnable {

		private SelectionKey sk;

		public RequestHandler(SelectionKey sk) {
			this.sk = sk;
		}

		@Override
		public void run() {

			SocketChannel sChannel = null;
			try {
				// 获取通道
				sChannel = (SocketChannel) sk.channel();
				// 声明保存客户端请求数据的缓冲区
				ByteBuffer buf = ByteBuffer.allocate(8192);
				// 读取数据并解析为字符串
				String requestBody = null;
				int len = sChannel.read(buf);
				if (len > 0) {
					buf.flip();
					requestBody = new String(buf.array(), 0, len);
					buf.clear();
				}
				if (requestBody == null) {
					return;
				}

				// 请求解码
				requestBody = URLDecoder.decode(requestBody,
						config.getRequestCharset());

				// debug
				if (log.isDebugEnabled()) {
					log.debug(requestBody);
				}

				// 创建请求对象
				Request req = new HttpRequest(requestBody);

				// 关闭输入
				sChannel.shutdownInput();

				// 根据uri获取处理请求的Servlet类型
				Class<? extends Servlet> servletClass = config.getServlet(req
						.getRequestURI());

				// 创建响应对象
				Response resp = null;

				// 动态请求
				if (servletClass != null) {
					// 获取一下session
					Session session = req.getSession();
					try {
						Servlet servlet = servletClass.newInstance();
						resp = new HttpResponse(req, sChannel, false);

						// 执行动态资源方法并获取响应视图
						View view = servlet.service(req, resp);
						// 渲染视图
						resp.render(view);

						resp.setResponseCode(ResponseUtil.RESPONSE_CODE_200);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						resp.setResponseCode(ResponseUtil.RESPONSE_CODE_500);
					}
					// 把session的cookie写出去
					Cookie sessionCookie = new Cookie(
							ResponseUtil.SESSION_ID_KEY, session.getId(), -1);
					resp.addCookie(sessionCookie);
					// 保存session
					SessionManager m = config.getSessionManager();
					m.saveSession(session);
				} else {
					// 静态请求
					resp = new HttpResponse(req, sChannel, true);
				}

				// 输出响应
				resp.response();

			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} finally {
				// 关闭通道
				try {
					sk.cancel();
					socketChannels.remove(sChannel.hashCode());
					if (sChannel.isConnected()) {
						sChannel.finishConnect();
					}
					sChannel.close();
					// debug
					if (log.isDebugEnabled()) {
						log.debug(String.format("Close connection from %s:%s",
								sChannel.socket().getInetAddress()
										.getHostAddress(), sChannel.socket()
										.getPort()));
					}
					sChannel = null;
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
}
