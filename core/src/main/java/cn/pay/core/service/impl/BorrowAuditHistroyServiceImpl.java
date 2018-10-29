package cn.pay.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pay.core.entity.business.BorrowAuditHistroy;
import cn.pay.core.repository.BorrowAuditHistroyRepository;
import cn.pay.core.service.BorrowAuditHistroyService;

/**
 * 借款审核历史服务实现
 * 
 * @author Qiujian
 * @date 2018年8月10日
 */
@Service
public class BorrowAuditHistroyServiceImpl implements BorrowAuditHistroyService {

	@Autowired
	private BorrowAuditHistroyRepository repository;

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void saveAndUpdate(BorrowAuditHistroy borrowAuditHistroy) {
		repository.saveAndFlush(borrowAuditHistroy);
	}

	@Override
	public List<BorrowAuditHistroy> getByBorrowId(Long borrowId) {
		return repository.findByBorrowId(borrowId);
	}

}
