package com.example.config.listener;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.Employee;
import com.example.service.EmployeeServiceImpl;
import com.example.service.PermissionServiceImpl;

/**
 * 应用启动监听
 * 
 * @author Qiujian
 *
 */
@Configuration
public class ContextStartListener implements ApplicationListener<ContextRefreshedEvent> {
	
	public static final String PEMISSION_MAP = "PEMISSION_MAP";

	@Autowired
	private EmployeeServiceImpl employeeService;
	
	@Autowired
	private PermissionServiceImpl permissionService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Transactional(rollbackFor = RuntimeException.class)
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		permissionService.settingPermissionMap();
		
		// 判断数据库中是否有超级管理员,没有新建一个
		boolean hasAdmin = employeeService.hasAdmin();
		if (hasAdmin) {
			return;
		}
		Employee employee = new Employee();
		employee.setUsername("admin");
		employee.setPassword(passwordEncoder.encode(
				new StringBuilder(20).append(Employee.INIT_EMPLOYEE_NUMBER).append(Employee.INIT_PASSWORD_SUFFIX)));
		employee.setEmail("719749187@qq.com");
		employee.setNickname("超级管理员");
		employee.setStatus(Employee.NORMAL_STATUS);
		employee.setSuperAdmin(Employee.IS_ADMIN);
		employee.setPasswordErrors(Employee.PASSWORD_ERRORS_INIT);
		employee.setEmployeeType(Employee.ADMIN_TYPE);
		employee.setEmployeeNumber(Employee.INIT_EMPLOYEE_NUMBER);
		Date date = new Date();
		employee.setCreateTime(date);
		employee.setUpdateTime(date);
		employee.setIntro("超级管理员");
		employeeService.save(employee);
	}

}
