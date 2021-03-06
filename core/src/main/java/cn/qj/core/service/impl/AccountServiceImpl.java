package cn.qj.core.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.qj.core.common.LogicException;
import cn.qj.core.entity.Account;
import cn.qj.core.repository.AccountRepository;
import cn.qj.core.service.AccountService;

/**
 * 账户服务实现
 * 
 * @author Qiujian
 * @date 2018/8/10
 */
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository repository;

	@Override
	public Account get(Long id) {
		Account account = repository.findOne(id);
		if (!account.checkVerifyKey()) {
			throw new LogicException("账户金额出现异常，阻止所有业务操作！");
		}
		return account;
	}

	@Override
	@Transactional(rollbackFor = { LogicException.class })
	public void update(Account account) {
		Account a = repository.saveAndFlush(account);
		if (a == null) {
			throw new LogicException("用户账户信息更新乐观锁异常");
		}
	}

	@Override
	@Transactional(rollbackFor = { LogicException.class })
	public void save(Account account) {
		repository.saveAndFlush(account);
	}

	@Override
	public void checkAccountChange() {
		List<Account> list = repository.findAll();
		for (Account account : list) {
			if (!account.checkVerifyKey()) {
				throw new LogicException("用户账户金额出现异常，阻止所有业务操作！");
			}
		}
	}

	@Override
	public void flushAccountVerify() {
		List<Account> list = repository.findAll();
		for (Account account : list) {
			update(account);
		}
	}

}
