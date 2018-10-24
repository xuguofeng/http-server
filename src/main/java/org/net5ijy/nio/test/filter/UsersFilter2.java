package org.net5ijy.nio.test.filter;

import org.net5ijy.nio.http.filter.Filter;
import org.net5ijy.nio.http.filter.FilterChain;
import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;

public class UsersFilter2 implements Filter {

	@Override
	public View doFilter(Request request, Response response,
			FilterChain filterChain) throws Exception {

		System.out.println("进入filter2");

		View view = filterChain.doFilter(request, response);

		System.out.println("离开filter2");

		return view;
	}
}
