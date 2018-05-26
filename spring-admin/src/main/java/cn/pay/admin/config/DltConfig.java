package cn.pay.admin.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.jsp.TaglibFactory;

/**
 * Dlt配置 让Freemaker模板使用Security标签
 * 
 * @author Administrator
 *
 */
@Configuration
public class DltConfig {
	@Autowired
	private FreeMarkerConfigurer configurer;

	@PostConstruct
	public void freeMarkerConfigurer() {
		List<String> tlds = new ArrayList<String>();
		tlds.add("/static/tags/security.tld");
		TaglibFactory taglibFactory = configurer.getTaglibFactory();
		taglibFactory.setClasspathTlds(tlds);
		if (taglibFactory.getObjectWrapper() == null) {
			taglibFactory.setObjectWrapper(configurer.getConfiguration().getObjectWrapper());
		}
	}
}
