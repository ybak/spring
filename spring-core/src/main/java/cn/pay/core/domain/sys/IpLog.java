
package cn.pay.core.domain.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.pay.core.domain.base.BaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用戶Ip日志
 * 
 * @author Qiujian
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "ip_log")
public class IpLog extends BaseDomain {
	private static final long serialVersionUID = 1L;
	public static final int SUCCESS = 1;
	public static final int FAIL = 0;

	@Column(name = "username")
	private String username;
	@Column(name = "login_state")
	private Integer loginState = IpLog.FAIL;
	@Column(name = "ip")
	private String ip;
	@Column(name = "login_time")
	private Date loginTime;
	@Column(name = "user_type")
	private Integer userType;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.id;
	}

	@Transient
	public String getDisplayState() {
		return loginState == IpLog.FAIL ? "登陆失败" : "登陆成功";
	}
}
