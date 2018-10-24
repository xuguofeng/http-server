package org.net5ijy.nio.http.filter;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;

public interface Filter {

	public View doFilter(Request request, Response response,
			FilterChain filterChain) throws Exception;
}
