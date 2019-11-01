package com.example.config.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import com.example.util.DataSourceUtil;

/**
 * 设置数据源
 * 
 * @author Qiu Jian
 *
 */
@Aspect
@Configuration
public class SetDataSourceAspect {

	@Around("@annotation(dataSourceKey)")
	public Object aroundSetDataSource(ProceedingJoinPoint joinPoint, DataSourceKey dataSourceKey) {
		Object obj = null;
		try {
			DataSourceUtil.setKey(dataSourceKey.value());
			obj = joinPoint.proceed();
			DataSourceUtil.remove();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return obj;
	}

}
