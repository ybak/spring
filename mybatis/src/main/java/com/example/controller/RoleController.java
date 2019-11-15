package com.example.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.LogicException;
import com.example.common.Result;
import com.example.entity.Role;
import com.example.service.RoleServiceImpl;
import com.example.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 角色控制器
 * 
 * @author Qiu Jian
 *
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController {

	@Autowired
	private RoleServiceImpl roleService;

	@GetMapping("/addRole")
	public Result addRole(Role role) {
		String roleName = role.getRoleName();
		Result verifyRoleName = this.verifyRoleName(roleName);
		if (verifyRoleName != null) {
			return verifyRoleName;
		}

		String intro = role.getIntro();
		if (StrUtil.hasText(intro)) {
			Result verifyIntro = this.verifyIntro(role.getIntro());
			if (verifyIntro != null) {
				return verifyIntro;
			}
		}

		Date date = new Date();
		role.setCreateTime(date);
		role.setUpdateTime(date);

		try {
			boolean hasRoleByRoleName = roleService.hasRoleByRoleName(roleName);
			if (hasRoleByRoleName) {
				return new Result(false, "角色名已存在");
			}
			int save = roleService.save(role);
			if (save != 1) {
				return new Result(false, "添加失败");
			}
			return new Result(true, "添加成功");
		} catch (Exception e) {
			log.error("系统异常", e);
			return new Result(false, "添加失败");
		}

	}

	@GetMapping("/allotPermission")
	public Result allotPermission(Long roleId, Long[] permissionIdList) {
		Result verifyId = this.verifyId(roleId);
		if (verifyId != null) {
			return verifyId;
		}

		if (permissionIdList == null || permissionIdList.length == 0) {
			return new Result(false, "权限ID组不能为空");
		}

		try {
			roleService.allotPermission(roleId, permissionIdList);
			return new Result(true, "分配成功");
		} catch (LogicException e) {
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			log.error("系统异常", e);
			return new Result(false, "分配失败");
		}

	}

	@GetMapping("/updateRole")
	public Result updateRole(Role role) {
		Long id = role.getId();
		Result verifyId = this.verifyId(id);
		if (verifyId != null) {
			return verifyId;
		}

		String roleName = role.getRoleName();
		Result verifyRoleName = this.verifyRoleName(roleName);
		if (verifyRoleName != null) {
			return verifyRoleName;
		}

		String intro = role.getIntro();
		if (StrUtil.hasText(intro)) {
			Result verifyIntro = this.verifyIntro(role.getIntro());
			if (verifyIntro != null) {
				return verifyIntro;
			}
		}

		role.setUpdateTime(new Date());
		try {
			String oldRoleName = roleService.getRoleNameById(id);
			if (StrUtil.noText(oldRoleName)) {
				return new Result(false, "角色ID不正确");
			}

			if (!oldRoleName.equals(roleName)) {
				boolean hasRoleByRoleName = roleService.hasRoleByRoleName(roleName);
				if (hasRoleByRoleName) {
					return new Result(false, "角色名已存在");
				}
			}

			int updateById = roleService.updateById(role);
			if (updateById != 1) {
				return new Result(false, "更新失败");
			}
			return new Result(true, "更新成功");
		} catch (Exception e) {
			log.error("系统异常", e);
			return new Result(false, "更新失败");
		}

	}

	private Result verifyId(Long id) {
		if (id == null) {
			return new Result(false, "角色ID不能为空");
		} else if (id.toString().length() > 20) {
			return new Result(false, "角色ID过长");
		}
		return null;
	}

	private Result verifyRoleName(String roleName) {
		if (StrUtil.noText(roleName)) {
			return new Result(false, "角色名不能为空");
		} else if (roleName.length() > 20) {
			return new Result(false, "角色名过长");
		} else if (StrUtil.isContainSpecialChar(roleName)) {
			return new Result(false, "角色名不能包含特殊字符");
		}
		return null;
	}

	private Result verifyIntro(String intro) {
		if (intro.length() > 255) {
			return new Result(false, "角色描述过长");
		}
		if (StrUtil.isContainSpecialChar(intro)) {
			return new Result(false, "角色描述不能包含特殊字符");
		}
		return null;
	}
}
