package cn.qj.core.pojo.event;

import org.springframework.context.ApplicationEvent;

import cn.qj.core.entity.RealAuth;
import lombok.Getter;

/**
 * 实名认证成功事件
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Getter
public class RealAuthEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	private RealAuth eventObj;

	public RealAuthEvent(Object source, RealAuth eventObj) {
		super(source);
		this.eventObj = eventObj;
	}

}
