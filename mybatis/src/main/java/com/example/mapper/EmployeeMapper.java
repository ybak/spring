package com.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;
import org.apache.ibatis.type.JdbcType;

import com.example.dto.ChangePasswordDto;
import com.example.entity.Employee;
import com.example.mapper.provider.EmployeeSqlProvider;
import com.example.model.ResetPasswordModel;
import com.example.vo.EmployeeVo;

/**
 * 员工数据操作
 * 
 * @Options(useGeneratedKeys = true, keyProperty = "id") 插入记录返回Id到对象
 * @SelectKey(before = false, keyProperty = "id", resultType = Long.class,
 *                   statement = { "SELECT LAST_INSERT_ID()" }) 先查询下一个Id封装到对象
 *
 * @author Qiu Jian
 *
 */
public interface EmployeeMapper {

	@Delete({ "DELETE  ", //
			"FROM ", //
			"	employee  ", //
			"WHERE ", //
			"	id = #{id,jdbcType=BIGINT}" })
	int deleteByPrimaryKey(Long id);

	@InsertProvider(type = EmployeeSqlProvider.class, method = "insertSelective")
	int insertSelective(Employee record);

	@UpdateProvider(type = EmployeeSqlProvider.class, method = "updateByPrimaryKeySelective")
	int updateByPrimaryKeySelective(Employee record);

	@Select({ "SELECT ", //
			"	count( * )  ", //
			"FROM ", //
			"	`employee`  ", //
			"WHERE ", //
			"	super_admin = 1" })
	int countBySuperAdmin();

	@Select({ "SELECT ", //
			"	id, ", //
			"	username, ", //
			"	`PASSWORD`, ", //
			"	email, ", //
			"	nickname, ", //
			"	password_errors, ", //
			"	`STATUS`, ", //
			"	super_admin, ", //
			"	employee_type, ", //
			"	employee_number, ", //
			"	intro, ", //
			"	lock_time  ", //
			"FROM ", //
			"	employee  ", //
			"WHERE ", //
			"	username = #{username,jdbcType=VARCHAR}" })
	@Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "username", property = "username", jdbcType = JdbcType.VARCHAR),
			@Result(column = "password", property = "password", jdbcType = JdbcType.VARCHAR),
			@Result(column = "email", property = "email", jdbcType = JdbcType.VARCHAR),
			@Result(column = "nickname", property = "nickname", jdbcType = JdbcType.VARCHAR),
			@Result(column = "password_errors", property = "passwordErrors", jdbcType = JdbcType.INTEGER),
			@Result(column = "status", property = "status", jdbcType = JdbcType.INTEGER),
			@Result(column = "super_admin", property = "superAdmin", jdbcType = JdbcType.INTEGER),
			@Result(column = "employee_type", property = "employeeType", jdbcType = JdbcType.INTEGER),
			@Result(column = "employee_number", property = "employeeNumber", jdbcType = JdbcType.VARCHAR),
			@Result(column = "intro", property = "intro", jdbcType = JdbcType.VARCHAR),
			@Result(column = "lock_time", property = "lockTime", jdbcType = JdbcType.TIMESTAMP),
			@Result(column = "id", property = "authorities", many = @Many(select = "com.example.mapper.PermissionMapper.selectPermissionVoByEmployeeId", fetchType = FetchType.LAZY)) })
	EmployeeVo selectEmployeeVoByUsername(String username);

	@Update({ "UPDATE employee  ", //
			"SET password_errors = #{passwordErrors,jdbcType=INTEGER}, ", //
			"update_time = #{updateTime,jdbcType=TIMESTAMP} ", //
			"WHERE ", //
			"	id = #{id,jdbcType=BIGINT}" })
	int updatePasswordErrorsAndUpdateTimeById(Employee employee);

	@Update({ "UPDATE employee  ", //
			"SET password_errors = #{passwordErrors,jdbcType=INTEGER}, ", //
			"`status` = #{status,jdbcType=INTEGER}, ", //
			"lock_time = #{lockTime,jdbcType=TIMESTAMP}, ", //
			"update_time = #{updateTime,jdbcType=TIMESTAMP} ", //
			"WHERE ", //
			"	id = #{id,jdbcType=BIGINT}" })
	int updatePasswordErrorsAndStatusAndLockTimeAndUpdateTimeById(Employee employee);

	@Select({ "SELECT ", //
			"	id, ", //
			"	password_errors, ", //
			"	`STATUS`  ", //
			"FROM ", //
			"	employee  ", //
			"WHERE ", //
			"	username = #{username,jdbcType=VARCHAR}" })
	@Results({ @Result(column = "id", property = "id", jdbcType = JdbcType.BIGINT, id = true),
			@Result(column = "password_errors", property = "passwordErrors", jdbcType = JdbcType.INTEGER),
			@Result(column = "status", property = "status", jdbcType = JdbcType.INTEGER) })
	Employee selectPasswordErrorsAndIdAndStatusByUsername(String username);

	@Select({ "SELECT ", //
			"	count( * )  ", //
			"FROM ", //
			"	`employee`  ", //
			"WHERE ", //
			"	username = #{username,jdbcType=VARCHAR}" })
	int countByUsername(String username);

	@Select({ "SELECT ", //
			"	count( * )  ", //
			"FROM ", //
			"	`employee`  ", //
			"WHERE ", //
			"	employee_number = #{employeeNumber,jdbcType=VARCHAR}" })
	int countByEmployeeNumber(String employeeNumber);

	@Update({ "UPDATE `employee`  ", //
			"SET `password` = #{password,jdbcType=VARCHAR}, ", //
			"update_time = #{updateTime,jdbcType=TIMESTAMP} ", //
			"WHERE ", //
			"	username = #{username,jdbcType=VARCHAR} ", //
			"	AND employee_number = #{employeeNumber,jdbcType=VARCHAR}" })
	int updatePasswordAndUpdateTimeByUsernameEmployeeNumber(ResetPasswordModel resetPasswordModel);

	@Update({ "UPDATE `employee`  ", //
			"SET `password` = #{password,jdbcType=VARCHAR}, ", //
			"update_time = #{updateTime,jdbcType=TIMESTAMP} ", //
			"WHERE ", //
			"	username = #{username,jdbcType=VARCHAR}" })
	int updatePasswordAndUpdateTimeByUsername(ChangePasswordDto changePasswordDto);

}