package cn.qj.key.config.datasource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 写数据源属性
 * 
 * @author Qiujian
 * @date 2018/9/25
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties("datasource.write")
public class WriteDataSourceConfig {
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
}
