package cn.qj.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * 模板工具
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
public class TemplateUtil {
	private TemplateUtil() {
	}

	private static final Logger logger = LoggerFactory.getLogger(TemplateUtil.class);

	private static final String TEMPLATE_PATH = "C:/Users/Administrator/git/spring/core/src/main/resources/java_template/";
	private static final String DAO_PATH = "C:/Users/Administrator/git/spring/core/src/main/java/cn/qj/core/repository/";
	private static final String SERVICE_PATH = "C:/Users/Administrator/git/spring/core/src/main/java/cn/qj/core/service/";

	public static void createJavaFile(Class<?> clz) throws TemplateNotFoundException, MalformedTemplateNameException,
			ParseException, IOException, TemplateException {
		String simpleName = clz.getSimpleName();
		Map<String, String> map = new HashMap<>(1);
		map.put("className", simpleName);
		@SuppressWarnings("deprecation")
		Configuration configuration = new Configuration();
		// 读取模板目录
		configuration.setDirectoryForTemplateLoading(new File(TEMPLATE_PATH));
		// 生成dao文件
		Writer daoWiter = new OutputStreamWriter(new FileOutputStream(DAO_PATH + simpleName + "Repository.java"),
				"utf-8");
		Template daoTemplate = configuration.getTemplate("dao.ftl");
		daoTemplate.process(map, daoWiter);

		// 生成service文件
		Writer serviceWiter = new OutputStreamWriter(new FileOutputStream(SERVICE_PATH + simpleName + "Service.java"),
				"utf-8");
		Template serviceTemplate = configuration.getTemplate("service.ftl");
		serviceTemplate.process(map, serviceWiter);
		Writer serviceImplWiter = new OutputStreamWriter(
				new FileOutputStream(SERVICE_PATH + "impl/" + simpleName + "ServiceImpl.java"), "utf-8");
		Template serviceImplTemplate = configuration.getTemplate("serviceImpl.ftl");
		serviceImplTemplate.process(map, serviceImplWiter);
	}

	/**
	 * 根据domain实体创建对应的dao service
	 * 
	 * @param clz
	 */
	public static void domainTemplate(Class<?> clz) {
		try {
			TemplateUtil.createJavaFile(clz);
		} catch (TemplateNotFoundException e) {
			logger.error(e.getMessage());
		} catch (MalformedTemplateNameException e) {
			logger.error(e.getMessage());
		} catch (ParseException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (TemplateException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
