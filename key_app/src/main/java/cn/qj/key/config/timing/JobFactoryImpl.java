package cn.qj.key.config.timing;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * job注入到Spring
 * 
 * @author Qiujian
 * @date 2018/10/30
 */
@Component
public class JobFactoryImpl extends AdaptableJobFactory {

	@Autowired
	private AutowireCapableBeanFactory capableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		// 调用父类的方法 quartzJob工厂中Job
		Object jobInstance = super.createJobInstance(bundle);
		// 装配到spring容器
		capableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}
}
