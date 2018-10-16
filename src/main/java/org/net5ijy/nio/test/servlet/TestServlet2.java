package org.net5ijy.nio.test.servlet;

import java.util.HashMap;
import java.util.Map;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.util.StringUtil;

public class TestServlet2 implements Servlet {

	@Override
	public View service(Request request, Response response) throws Exception {

		response.setContentType("text/html;charset=utf-8");

		Map<String, Object> user = new HashMap<String, Object>();
		user.put("name", "徐国峰");
		user.put("age", 28);
		user.put("birthday", StringUtil.parseDate("1990-09-16"));

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("user", user);

		View view = new View("test/test2.ftl", map);

		return view;
	}
}
