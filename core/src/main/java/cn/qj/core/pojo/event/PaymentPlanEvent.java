package cn.qj.core.pojo.event;

import org.springframework.context.ApplicationEvent;

import cn.qj.core.entity.PaymentPlan;
import lombok.Getter;

/**
 * 收款成功事件
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Getter
public class PaymentPlanEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;

	private PaymentPlan eventObj;

	public PaymentPlanEvent(Object source, PaymentPlan eventObj) {
		super(source);
		this.eventObj = eventObj;
	}

}
