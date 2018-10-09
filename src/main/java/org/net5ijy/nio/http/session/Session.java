package org.net5ijy.nio.http.session;

public interface Session {

	public long getMaxInactiveIntervalSeconds();

	public void setMaxInactiveIntervalSeconds(long maxInactiveIntervalSeconds);

	public String getId();

	public long getCreateTime();

	public long getInactiveTime();

	public void refreshInactiveTime();

	public void setAttribute(Object name, Object value);

	public Object getAttribute(Object name);

	public Object removeAttribute(Object name);
}
