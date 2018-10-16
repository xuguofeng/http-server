package org.net5ijy.nio.test.servlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.net5ijy.nio.http.request.Request;
import org.net5ijy.nio.http.response.Response;
import org.net5ijy.nio.http.response.view.View;
import org.net5ijy.nio.http.servlet.Servlet;
import org.net5ijy.nio.test.bean.User;

public class UsersServlet implements Servlet {

	@Override
	public View service(Request request, Response response) throws Exception {
		List<User> users = getUsers();
		Map<Object, Object> model = new HashMap<Object, Object>();
		model.put("users", users);
		View view = new View("user/users.ftl", model);
		return view;
	}

	private List<User> getUsers() {

		String sql = "select id, username, mobile, email, birthday, create_time from test.mp_user";

		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;

		try {

			conn = getConnection();

			prep = conn.prepareStatement(sql);

			rs = prep.executeQuery();

			List<User> users = new ArrayList<User>();

			while (rs.next()) {

				User u = new User();
				u.setId(rs.getInt(1));
				u.setUsername(rs.getString(2));
				u.setMobile(rs.getString(3));
				u.setEmail(rs.getString(4));
				u.setBirthday(rs.getDate(5));
				u.setCreateTime(rs.getTimestamp(6));

				users.add(u);
			}

			return users;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (prep != null) {
				try {
					prep.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
		return new ArrayList<User>();
	}

	private Connection getConnection() throws ClassNotFoundException,
			SQLException {

		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/test";
		String username = "system";
		String password = "123456";

		Class.forName(driver);

		return DriverManager.getConnection(url, username, password);
	}
}
