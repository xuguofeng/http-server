package org.net5ijy.nio.test.filter;

import org.net5ijy.nio.http.filter.Filter;
import org.net5ijy.nio.http.filter.FilterChain;
import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.session.Session;

public class LoginFilter implements Filter {

	@Override
	public View doFilter(Request request, Response response,
			FilterChain filterChain) throws Exception {

		System.out.println("进入" + this.getClass().getSimpleName());

		// 获取Session保存的user数据
		Session s = request.getSession();
		Object user = s.getAttribute("user");
		System.out.println(this.getClass().getName() + " - " + user);

		View view = filterChain.doFilter(request, response);

		System.out.println("离开" + this.getClass().getSimpleName());

		return view;
	}
}
