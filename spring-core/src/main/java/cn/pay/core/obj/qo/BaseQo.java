package cn.pay.core.obj.qo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础查询对象
 * 
 * @author Qiujian
 *
 */
@Getter
@Setter
public class BaseQo implements  Serializable{
	private static final long serialVersionUID = 1L;
	private Integer currentPage = 1;
	private Integer pageSize = 5;

	public Integer getCurrentPage() {
		return currentPage;
	}
}
