package cn.pay.core.domain.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 字典明细
 * 
 * @author Administrator
 *
 */
@Setter
@Getter
@ToString
@Entity
public class SystemDictionaryItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long systemDictionaryId;
	private String title;
	private String intro;
	private Integer sequence;
	private Date gmtCreate;
	private Date gmtModified;
	

	public String getJsonString() {
		Map<String, Object> json = new HashMap<>(5);
		json.put("id", id);
		json.put("systemDictionaryId", systemDictionaryId);
		json.put("title", title);
		json.put("sequence", sequence);
		json.put("intro", intro);
		return JSONObject.toJSONString(json);
	}
}
