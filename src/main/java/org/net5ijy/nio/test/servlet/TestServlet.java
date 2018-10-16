package org.net5ijy.nio.test.servlet;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.nio.http.session.Session;

public class TestServlet implements Servlet {

	@Override
	public View service(Request request, Response response) throws Exception {

		Session s = request.getSession();

		Object user = s.getAttribute("user");

		System.out.println(user);

		s.setAttribute("user", "user");

		response.setContentType("text/html;charset=utf-8");

		response.println("<!DOCTYPE html>");
		response.println("<html>");
		response.println("<head>");
		response.println("<meta charset=\"UTF-8\">");
		response.println("<title>NIO HTTP 服务器</title>");
		response.println("</head>");
		response.println("<body>");
		response.println("<h1>NIO HTTP 服务器</h1>");
		response.println("<h2>使用链接下载文件</h2>");
		response.println("<a target=\"_blank\" href=\"attachment/备份.zip\">备份.zip</a><br />");
		response.println("<a target=\"_blank\" href=\"attachment/备份.txt\">备份.txt</a><br />");
		response.println("<a target=\"_blank\" href=\"attachment/backup.zip\">backup.zip</a><br />");
		response.println("<a target=\"_blank\" href=\"attachment/backup.txt\">backup.txt</a><br />");
		response.println("<a target=\"_blank\" href=\"attachment/http.out.20180828.7z\">http.out.20180828.7z</a><br />");
		response.println("<h2>测试表单提交</h2>");
		response.println("<form action=\"index.html\" method=\"get\">");
		response.println("	姓名：<input name=\"name\" value=\"测试账号\"><br />");
		response.println("	年龄：<input name=\"age\" value=\"27\"><br />");
		response.println("	时间：<input name=\"birthday\" value=\"2018-08-29 10:54:00\"><br />");
		response.println("	<input type=\"submit\" value=\"提交\" >");
		response.println("</form>");
		response.println("</body>");
		response.println("</html>");

		return null;
	}
}
