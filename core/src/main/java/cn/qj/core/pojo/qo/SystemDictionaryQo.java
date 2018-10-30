package cn.qj.core.pojo.qo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统字典查询对象
 * 
 * @author Qiujian
 *
 */
@Setter
@Getter
@ToString
public class SystemDictionaryQo extends PageConditionQo {
	private static final long serialVersionUID = 1L;
	private String keyword;
}