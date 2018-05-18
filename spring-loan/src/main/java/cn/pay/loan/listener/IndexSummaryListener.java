package cn.pay.loan.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import cn.pay.core.service.IndexService;

@Component
public class IndexSummaryListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private IndexService indexService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		indexService.updateIndexSummaryVO();
	}

}
