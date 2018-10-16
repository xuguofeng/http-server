package org.net5ijy.nio.http.response.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

/**
 * 动态资源视图解析器
 * 
 * @author 创建人：xuguofeng
 * @version 创建于：2018年10月16日 下午2:49:08
 */
public class ViewResovler {

	private Configuration freeMarkerConfig = new Configuration(new Version(
			"2.3.0"));

	private Map<String, Template> viewTemplates = new HashMap<String, Template>();

	private static ViewResovler viewResovler = new ViewResovler();

	public static ViewResovler getViewResovler() {
		return viewResovler;
	}

	private ViewResovler() {

	}

	public String resolveView(File templateFile, Map<Object, Object> model)
			throws FileNotFoundException, IOException {

		// 获取模板
		Template t = viewTemplates.get(templateFile.getAbsolutePath());

		if (t == null) {
			t = new Template(templateFile.getAbsolutePath(),
					new BufferedReader(new InputStreamReader(
							new FileInputStream(templateFile))),
					freeMarkerConfig);
			// viewTemplates.put(templateFile.getAbsolutePath(), t);
		}

		// 解析模板
		StringWriter sw = new StringWriter();

		try {
			t.process(model, sw);
			return sw.toString();
		} catch (TemplateException e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			// 关闭字符串流
			sw.close();
		}
	}
}
