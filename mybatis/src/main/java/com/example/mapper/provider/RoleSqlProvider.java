package com.example.mapper.provider;

import com.example.entity.Role;
import com.example.qo.RoleQo;
import com.example.util.SqlUtil;
import com.example.util.StrUtil;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SQL;

/**
 * 角色sql供应
 * 
 * @author Qiu Jian
 *
 */
public class RoleSqlProvider {

	public String insert(Role record) {
		SQL sql = new SQL();
		sql.INSERT_INTO("role");
		sql.VALUES("role_name", "#{roleName,jdbcType=VARCHAR}");
		if (record.getIntro() != null) {
			sql.VALUES("intro", "#{intro,jdbcType=VARCHAR}");
		}
		sql.VALUES("create_time", "#{createTime,jdbcType=TIMESTAMP}");
		sql.VALUES("update_time", "#{updateTime,jdbcType=TIMESTAMP}");
		return sql.toString();
	}

	public String updateById(Role record) {
		SQL sql = new SQL();
		sql.UPDATE("role");
		sql.SET("role_name = #{roleName,jdbcType=VARCHAR}");
		sql.SET("intro = #{intro,jdbcType=VARCHAR}");
		sql.SET("update_time = #{updateTime,jdbcType=TIMESTAMP}");
		sql.WHERE("id = #{id,jdbcType=BIGINT}");

		return sql.toString();
	}

	public String deleteRolePermissionByPermissionIdList(Map<String, List<Long>> params) {
		SQL sql = new SQL();
		sql.DELETE_FROM("role_permission");
		sql.WHERE(SqlUtil.inJoint("permission_id", "list", params.get("list")));
		return sql.toString();
	}

	public String selectByQo(RoleQo roleQo) {
		SQL sql = new SQL();
		sql.SELECT("id", "role_name", "intro");
		sql.FROM("role");
		if (StrUtil.hasText(roleQo.getRoleName())) {
			sql.WHERE("role_name=#{roleName,jdbcType=VARCHAR}");
		}
		return sql.toString();
	}
}