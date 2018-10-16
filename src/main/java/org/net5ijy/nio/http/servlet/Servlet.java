package org.net5ijy.nio.http.servlet;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;

public interface Servlet {

	View service(Request request, Response response) throws Exception;
}
