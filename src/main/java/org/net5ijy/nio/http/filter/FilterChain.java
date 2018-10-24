package org.net5ijy.nio.http.filter;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;

public class FilterChain {

	private Filter filter;
	private FilterChain nextFilterChain;
	private Servlet servlet;

	public FilterChain(Filter filter, FilterChain nextFilterChain,
			Servlet servlet) {
		super();
		this.filter = filter;
		this.nextFilterChain = nextFilterChain;
		this.servlet = servlet;
	}

	public View doFilter(Request request, Response response) throws Exception {
		if (filter != null) {
			return filter.doFilter(request, response, nextFilterChain);
		}
		return servlet.service(request, response);
	}
}
