package cn.pay.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pay.core.entity.business.Account;
import cn.pay.core.entity.business.CompanyBankInfo;
import cn.pay.core.entity.business.Recharge;
import cn.pay.core.entity.sys.LoginInfo;
import cn.pay.core.pojo.event.RechargeEvent;
import cn.pay.core.pojo.qo.RechargeQo;
import cn.pay.core.pojo.vo.PageResult;
import cn.pay.core.repository.RechargeRepository;
import cn.pay.core.service.AccountFlowService;
import cn.pay.core.service.AccountService;
import cn.pay.core.service.CompanyBankInfoService;
import cn.pay.core.service.LoginInfoService;
import cn.pay.core.service.RechargeService;
import cn.pay.core.util.HttpServletContext;

/**
 * 充值服务实现
 * 
 * @author Qiujian
 * @date 2018年8月10日
 */
@Service
public class RechargeServiceImpl implements RechargeService {

	@Autowired
	private RechargeRepository repository;

	@Autowired
	private LoginInfoService loginInfoService;
	@Autowired
	private CompanyBankInfoService companyBankInfoService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountFlowService accountFlowService;

	@Autowired
	private ApplicationContext ac;

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void apply(Recharge recharge) {
		recharge.setApplier(HttpServletContext.getCurrentLoginInfo());
		recharge.setApplyTime(new Date());
		recharge.setState(Recharge.AUTH_NORMAL);
		repository.saveAndFlush(recharge);
	}

	@Override
	public PageResult list(RechargeQo qo) {
		Page<Recharge> page = repository.findAll(new Specification<Recharge>() {
			@Override
			public Predicate toPredicate(Root<Recharge> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if (qo.getState() != -1) {
					list.add(cb.equal(root.get("state").as(Integer.class), qo.getState()));
				}
				if (qo.getBeginDate() != null) {
					list.add(cb.greaterThanOrEqualTo(root.get("applyTime").as(Date.class), qo.getBeginDate()));
				}
				if (qo.getEndDate() != null) {
					list.add(cb.lessThanOrEqualTo(root.get("applyTime").as(Date.class), qo.getEndDate()));
				}
				if (qo.getApplierId() != null) {
					list.add(
							cb.equal(root.get("applier").as(LoginInfo.class), loginInfoService.getLoginInfoById(qo.getApplierId())));
				}
				if (qo.getTradeCode() != null) {
					list.add(cb.like(root.get("tradeCode"), qo.getTradeCode() + "%"));
				}
				if (qo.getBankInfoId() != -1) {
					list.add(cb.equal(root.get("bankInfo").as(CompanyBankInfo.class),
							companyBankInfoService.get(qo.getBankInfoId())));
				}
				Predicate[] ps = new Predicate[list.size()];
				return cb.and(list.toArray(ps));
			}
		}, new PageRequest(qo.getCurrentPage() - 1, qo.getPageSize(), Direction.DESC, "applyTime"));
		if (page.getTotalElements() < 1) {
			return PageResult.empty(qo.getPageSize());
		}
		return new PageResult(page.getContent(), page.getTotalPages(), qo.getCurrentPage(), qo.getPageSize());
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void audit(Long id, String remark, Integer state) {
		Recharge recharge = repository.findOne(id);
		if (recharge != null && recharge.getState().equals(Recharge.AUTH_NORMAL)) {
			recharge.setAuditor(HttpServletContext.getCurrentLoginInfo());
			recharge.setAuditTime(new Date());
			recharge.setState(state);
			recharge.setRemark(remark);
			if (state.equals(Recharge.AUTH_PASS)) {
				// 拿到用户账户对象
				Account account = accountService.get(recharge.getApplier().getId());
				// 修改账户可用余额
				account.setUsableAmount(account.getUsableAmount().add(recharge.getAmount()));
				// 创建充值流水
				accountFlowService.rechargeFolw(recharge, account);
				accountService.update(account);
			}
			repository.saveAndFlush((recharge));
			// 充值成功发送短信
			ac.publishEvent(new RechargeEvent(this, recharge));
		}
	}

}
