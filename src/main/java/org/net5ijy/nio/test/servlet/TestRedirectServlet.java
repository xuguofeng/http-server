package org.net5ijy.nio.test.servlet;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;

public class TestRedirectServlet implements Servlet {

	@Override
	public View service(Request request, Response response) throws Exception {
		response.setResponseCode(302);
		response.setHeader("Location", "http://www.qb178.com");
		return null;
	}
}
