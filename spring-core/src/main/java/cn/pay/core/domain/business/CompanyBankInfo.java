package cn.pay.core.domain.business;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 公司银行账号信息
 * 
 * @author Qiujian
 *
 */
@Setter
@Getter
@ToString
@Entity
public class CompanyBankInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String bankName;
	/** 账户名称 */
	private String accountName;
	/** 银行卡号 */
	private String bankNumber;
	/** 开户支行名称 */
	private String bankForkName;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	@Transient
	public String getJsonString() {
		Map<String, Object> json = new HashMap<String, Object>(6);
		json.put("id", getId());
		json.put("bankName", bankName);
		json.put("accountName", accountName);
		json.put("bankNumber", bankNumber);
		json.put("bankForkName", bankForkName);
		return JSONObject.toJSONString(json);
	}
}
