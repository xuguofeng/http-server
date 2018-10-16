package org.net5ijy.nio.http.response.view;

import java.io.Serializable;
import java.util.Map;

/**
 * 封装动态资源的响应视图信息
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年10月16日 上午9:07:31
 */
public class View implements Serializable {

	private static final long serialVersionUID = -3679982357713084586L;

	private String viewName;

	private Map<Object, Object> model;

	public View(String viewName, Map<Object, Object> model) {
		super();
		this.viewName = viewName;
		this.model = model;
	}

	public View() {
		super();
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<Object, Object> getModel() {
		return model;
	}

	public void setModel(Map<Object, Object> model) {
		this.model = model;
	}
}
