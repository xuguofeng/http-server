package org.net5ijy.nio.http.session;

public interface SessionManager {

	public Session getSession(String id);

	public void saveSession(Session session);

	public void setSessionTimeSeconds(int seconds);
}
