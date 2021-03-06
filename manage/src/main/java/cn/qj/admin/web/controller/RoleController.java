package cn.qj.admin.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.qj.core.entity.Role;
import cn.qj.core.service.RoleService;

/**
 * 角色控制器
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Controller
public class RoleController {

	@Autowired
	private RoleService service;

	@RequestMapping("/role/add")
	@ResponseBody
	public void addRole() {
		Role role = new Role();
		role.setUrl("/systemDictionary/page.do");
		role.setName("ROLE_系统字典列表查询");
		service.saveRole(role);
	}
}
