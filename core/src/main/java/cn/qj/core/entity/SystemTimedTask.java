package cn.qj.core.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

/**
 * 系统定时任务
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Data
@Entity
public class SystemTimedTask implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int PAUSE = 1;
	public static final int NORMAL = 0;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String jobName;
	/** 定时任务的组 */
	private String groupName;
	/** 计划任务表达式 */
	private String cronExpression;
	/** 描述 */
	private String description;
	/** 状态 */
	private int status;
	/** 创建时间 */
	private Date gmtCreate;
	/** 修改时间 */
	private Date gmtModified;

	public String getStatusDisplay() {
		return status == PAUSE ? "暂停" : "开启";
	}

	public String getJsonString() {
		Map<String, Object> json = new HashMap<>(5);
		json.put("id", id);
		json.put("jobName", jobName);
		json.put("groupName", groupName);
		json.put("cronExpression", cronExpression);
		json.put("description", description);
		return JSONObject.toJSONString(json);
	}
}