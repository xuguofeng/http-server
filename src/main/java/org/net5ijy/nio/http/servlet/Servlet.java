package org.net5ijy.nio.http.servlet;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;

public interface Servlet {

	void service(Request request, Response response) throws Exception;
}
