package org.net5ijy.nio.http.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSession implements Serializable, Session {

	private static final long serialVersionUID = 4671550808231103763L;

	public static final long DEFAULT_MAX_INACTIVE_INTERVAL = 1800;

	private String id;

	private long createTime;

	private long maxInactiveIntervalSeconds;

	private long inactiveTime;

	private Map<Object, Object> attribute = new HashMap<Object, Object>();

	public HttpSession() {
		this(DEFAULT_MAX_INACTIVE_INTERVAL);
	}

	public HttpSession(long maxInactiveIntervalSeconds) {
		super();
		this.id = UUID.randomUUID().toString().replace("-", "");
		this.createTime = System.currentTimeMillis();
		this.maxInactiveIntervalSeconds = maxInactiveIntervalSeconds;
		refreshInactiveTime();
	}

	public long getMaxInactiveIntervalSeconds() {
		return maxInactiveIntervalSeconds;
	}

	public void setMaxInactiveIntervalSeconds(long maxInactiveIntervalSeconds) {
		this.maxInactiveIntervalSeconds = maxInactiveIntervalSeconds;
		refreshInactiveTime();
	}

	public String getId() {
		return id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public long getInactiveTime() {
		return inactiveTime;
	}

	public void refreshInactiveTime() {
		inactiveTime = System.currentTimeMillis() + maxInactiveIntervalSeconds
				* 1000;
	}

	public void setAttribute(Object name, Object value) {
		attribute.put(name, value);
	}

	public Object getAttribute(Object name) {
		return attribute.get(name);
	}

	public Object removeAttribute(Object name) {
		return attribute.remove(name);
	}
}
