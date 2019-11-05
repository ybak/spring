package com.example.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 菜单
 * 
 * @author Qiu Jian
 *
 */
@Setter
@Getter
@ToString
public class Menu implements Serializable {
	private static final long serialVersionUID = -3821521926063766360L;

	private Long id;
	private String menuName;
	private String menuCode;
	private String intro;
	private List<Permission> pemissionList;
	private List<Menu> children;
	private List<Role> roleList;
	private Date createTime;
	private Date updateTime;
	private Long parentId;

}
