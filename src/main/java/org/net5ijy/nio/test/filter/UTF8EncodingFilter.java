package org.net5ijy.nio.test.filter;

import org.net5ijy.nio.http.filter.Filter;
import org.net5ijy.nio.http.filter.FilterChain;
import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;

public class UTF8EncodingFilter implements Filter {

	@Override
	public View doFilter(Request request, Response response,
			FilterChain filterChain) throws Exception {

		System.out.println("进入UTF8EncodingFilter");

		request.setCharEncoding("utf-8");

		View view = filterChain.doFilter(request, response);

		System.out.println("离开UTF8EncodingFilter");

		return view;
	}
}
