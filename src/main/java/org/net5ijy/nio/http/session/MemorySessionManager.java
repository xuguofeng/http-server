package org.net5ijy.nio.http.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemorySessionManager implements SessionManager {

	private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	private int sessionTimeSeconds;

	@Override
	public Session getSession(String id) {
		if (id == null) {
			return new HttpSession(sessionTimeSeconds);
		}
		Session session = sessions.remove(id);
		if (session != null
				&& System.currentTimeMillis() < session.getInactiveTime()) {
			return session;
		}
		session = new HttpSession(sessionTimeSeconds);
		return session;
	}

	@Override
	public void saveSession(Session session) {
		sessions.put(session.getId(), session);
	}

	@Override
	public void setSessionTimeSeconds(int seconds) {
		this.sessionTimeSeconds = seconds;
	}
}
