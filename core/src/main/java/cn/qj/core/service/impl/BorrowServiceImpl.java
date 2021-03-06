package cn.qj.core.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.qj.core.common.LogicException;
import cn.qj.core.common.PageResult;
import cn.qj.core.consts.BidConst;
import cn.qj.core.consts.StatusConst;
import cn.qj.core.entity.Account;
import cn.qj.core.entity.Bid;
import cn.qj.core.entity.Borrow;
import cn.qj.core.entity.BorrowAuditHistroy;
import cn.qj.core.entity.LoginInfo;
import cn.qj.core.entity.PaymentPlan;
import cn.qj.core.entity.RepaymentSchedule;
import cn.qj.core.entity.UserInfo;
import cn.qj.core.pojo.event.BorrowEvent;
import cn.qj.core.pojo.qo.BorrowQo;
import cn.qj.core.repository.BorrowRepository;
import cn.qj.core.service.AccountFlowService;
import cn.qj.core.service.AccountService;
import cn.qj.core.service.BidService;
import cn.qj.core.service.BorrowAuditHistroyService;
import cn.qj.core.service.BorrowService;
import cn.qj.core.service.LoginInfoService;
import cn.qj.core.service.PaymentPlanService;
import cn.qj.core.service.RepaymentScheduleService;
import cn.qj.core.service.SystemAccountService;
import cn.qj.core.service.UserInfoService;
import cn.qj.core.util.BidStateUtil;
import cn.qj.core.util.CalculatetUtil;
import cn.qj.core.util.DecimalFormatUtil;
import cn.qj.core.util.HttpSessionUtil;
import cn.qj.core.util.ResultUtil;
import cn.qj.core.util.StringUtil;

/**
 * 借款服务实现
 * 
 * @author Qiujian
 * @date 2018/8/10
 */
@Service
public class BorrowServiceImpl implements BorrowService {

	@Autowired
	private BorrowRepository repository;

	@Autowired
	private BorrowAuditHistroyService borrowAuditHistroyService;
	@Autowired
	private RepaymentScheduleService repaymentScheduleService;
	@Autowired
	private PaymentPlanService paymentPlanService;
	@Autowired
	private BidService bidService;
	@Autowired
	private AccountFlowService accountFlowService;
	@Autowired
	private SystemAccountService systemAccountService;
	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private LoginInfoService loginInfoService;

	@Autowired
	private ApplicationContext ac;

	@Resource
	private ValueOperations<String, List<Borrow>> valueOperations;

	@Override
	public boolean isApplyBorrow() {
		// 拿到当前用户信息
		UserInfo userInfo = userInfoService.get(HttpSessionUtil.getCurrentLoginInfo().getId());
		return userInfo.getIsBasicInfo()
				// 满足条件认证分
				&& userInfo.getAuthScore() >= BidConst.BORROW_CREDIT_SCORE
				// 没有借款在审核流程
				&& !userInfo.getHasBorrow()
				// 是实名认证
				&& userInfo.getIsRealAuth();
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void apply(Borrow borrow) {
		LoginInfo currentLoginInfo = HttpSessionUtil.getCurrentLoginInfo();
		Account account = accountService.get(currentLoginInfo.getId());
		if (isApplyBorrow()
				// 借款金额<=剩余信用额度
				&& borrow.getAmount().compareTo(account.getRemainBorrowLimit()) <= 0
				// 借款金额大于最低借款金额
				&& borrow.getAmount().compareTo(BidConst.MIN_BORROW_AMOUNT) >= 0
				&& borrow.getRate().compareTo(BidConst.MIN_BORROW_RETE) >= 0
				&& borrow.getRate().compareTo(BidConst.MAX_BORROW_RETE) <= 0
				// 设置最小投标金额需要大于系统设置的最小投标金额
				&& borrow.getMinBidAmount().compareTo(BidConst.MIN_BID_AMOUNT) >= 0) {
			borrow.setApplyTime(new Date());
			borrow.setDisableDate(new Date());
			// 处于发表前审核状态
			borrow.setState(BidConst.BORROW_STATE_PUBLISH_PENDING);
			borrow.setCreateUser(currentLoginInfo);
			borrow.setTotalInterestAmount(CalculatetUtil.calTotalInterest(borrow.getReturnType(), borrow.getAmount(),
					borrow.getRate(), borrow.getMonthReturn()));
			repository.saveAndFlush(borrow);
			UserInfo userInfo = userInfoService.get(currentLoginInfo.getId());
			// 设置借款状态
			userInfo.addState(BidStateUtil.OP_BORROW_PROCESS);
			userInfoService.update(userInfo);
		}
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void bid(Long borrowId, BigDecimal amount, Long loginInfoId) {
		// 拿到借款对象
		Borrow borrow = get(borrowId);
		// 1.检查标是否存在，检查是否是招标中，检查招标的时间是否到期
		if (borrow != null && borrow.getState() == BidConst.BORROW_STATE_BIDDING
				&& new Date().before(borrow.getDisableDate())) {
			// LoginInfo currentLoginInfo = HttpSessionContext.getCurrentLoginInfo();
			LoginInfo currentLoginInfo = loginInfoService.getLoginInfoById(loginInfoId);
			// 拿到投资者账户对象
			Account bidAccount = accountService.get(currentLoginInfo.getId());
			// 2.检查投资 投标金额小于可用金额 大于最小投标金额 小于剩余可投金额
			if (amount.compareTo(bidAccount.getUsableAmount()) <= 0 && amount.compareTo(borrow.getMinBidAmount()) >= 0
					&& amount.compareTo(borrow.getRemainAmount()) <= 0) {
				// 3.账户金额减少，冻结金额增加
				bidAccount.setUsableAmount(bidAccount.getUsableAmount().subtract(amount));
				bidAccount.setFreezedAmount(bidAccount.getFreezedAmount().add(amount));

				// 4.增加一个bid对象
				Bid bid = new Bid();
				bid.setActualRate(borrow.getRate());
				bid.setAmount(amount);
				bid.setBorrow(borrow);
				bid.setBorrowTitle(borrow.getTitle());
				bid.setCreateTime(new Date());
				bid.setCreateUser(currentLoginInfo);
				bidService.saveAndUpdate(bid);
				// 生成投标流水
				accountFlowService.bidFLow(bid, bidAccount);

				// 5.修改借款相关属性
				borrow.setBidCount(borrow.getBidCount() + 1);
				borrow.setCurrentSum(borrow.getCurrentSum().add(amount));

				// 6.判断标是否投满，投满进入满标一审状态
				if (borrow.getCurrentSum().equals(borrow.getAmount())) {
					borrow.setState(BidConst.BORROW_STATE_APPROVE_PENDING_1);
				}
				update(borrow);
				accountService.update(bidAccount);
			}
		} else {
			throw new LogicException("招标日期到期");
		}
	}

	@Override
	public PageResult list(BorrowQo qo) {
		Page<Borrow> page = repository.findAll(new Specification<Borrow>() {
			@Override
			public Predicate toPredicate(Root<Borrow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if (qo.getBorrowState() != -1) {
					list.add(cb.equal(root.get("state").as(Integer.class), qo.getBorrowState()));
				}
				if (qo.getStatus() != null && qo.getStatus().size() > 0) {
					In<Integer> in = cb.in(root.get("state"));
					for (Integer state : qo.getStatus()) {
						in.value(state);
					}
					list.add(in);
				}
				Predicate[] rs = new Predicate[list.size()];
				return cb.and(list.toArray(rs));
			}
		}, new PageRequest(qo.getCurrentPage() - 1, qo.getPageSize(), qo.getOrderType(),
				StringUtil.hasLength(qo.getOrderBy()) ? qo.getOrderBy() : "applyTime"));
		if (page.getTotalElements() < 1) {
			return ResultUtil.empty(qo.getPageSize());
		}
		return new PageResult(page.getContent(), page.getTotalPages(), qo.getCurrentPage(), qo.getPageSize());
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void publishAudit(Long id, Integer state, String remark) {
		// 得到当前用户借款对象
		Borrow borrow = get(id);
		if (borrow != null) {
			// 创建一个审核历史对象 每一次审核对应一条历史记录
			createBorrowAuditHistroy(state, borrow, remark, StatusConst.PUSH_AUDIT);
			if (state.equals(StatusConst.AUTH_PASS)) {
				// 修改状态进入招标中
				borrow.setState(BidConst.BORROW_STATE_BIDDING);
				// 修改发布时间
				borrow.setPublishTime(new Date());
				// 设置截止时间
				borrow.setDisableDate(DateUtils.addDays(borrow.getPublishTime(), borrow.getDisableDays()));
				// 设置风控意见
				borrow.setNote(remark);
			} else {
				// 发标前审核失败
				borrow.setState(BidConst.BORROW_STATE_PUBLISH_REFUSE);
				// 拿到当前用户借款对象 删除发标状态
				UserInfo userInfo = userInfoService.get(borrow.getCreateUser().getId());
				userInfo.deleteState(BidStateUtil.OP_BORROW_PROCESS);
				userInfoService.update(userInfo);
			}
			update(borrow);
		}
	}

	private void createBorrowAuditHistroy(int state, Borrow borrow, String remark, int auditType) {
		BorrowAuditHistroy histroy = new BorrowAuditHistroy();
		histroy.setApplier(borrow.getCreateUser());
		histroy.setApplyTime(borrow.getApplyTime());
		histroy.setAuditor(HttpSessionUtil.getCurrentLoginInfo());
		histroy.setAuditTime(new Date());
		histroy.setBorrowId(borrow.getId());
		histroy.setRemark(remark);
		histroy.setState(state);
		histroy.setAuditType(auditType);
		borrowAuditHistroyService.saveAndUpdate(histroy);
	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void audit1Audit(Long id, String remark, Integer state) {
		Borrow borrow = get(id);
		// 必须是满标一审状态
		if (borrow != null && borrow.getState() == BidConst.BORROW_STATE_APPROVE_PENDING_1) {
			// 创建审核历史记录
			createBorrowAuditHistroy(state, borrow, remark, StatusConst.FULL_AUDIT1);
			if (state.equals(StatusConst.AUTH_PASS)) {
				borrow.setState(BidConst.BORROW_STATE_APPROVE_PENDING_2);
			} else {
				// 退标 满标一审或者满标二审拒绝
				cancelBorrow(borrow, BidConst.BORROW_STATE_REJECTED);
			}

			// 更新借款对象
			update(borrow);
		}
	}

	private void cancelBorrow(Borrow borrow, Integer state) {
		// 修改借钱对象的状态为审核失败
		borrow.setState(state);

		// 更新用户没有借款在流程中
		UserInfo userInfo = userInfoService.get(borrow.getCreateUser().getId());
		userInfo.deleteState(BidStateUtil.OP_BORROW_PROCESS);
		userInfoService.update(userInfo);

		// 退钱
		Map<Long, Account> bidAccountMap = new HashMap<Long, Account>(borrow.getBidList().size());
		for (Bid bid : borrow.getBidList()) {
			// 获取投标用户的账户
			Account bidAccount = bidAccountMap.get(bid.getCreateUser().getId());
			if (bidAccount == null) {
				bidAccount = accountService.get(bid.getCreateUser().getId());
				bidAccountMap.put(bidAccount.getId(), bidAccount);
			}
			// 余额增加 冻结金额减少
			bidAccount.setUsableAmount(bidAccount.getUsableAmount().add(bid.getAmount()));
			bidAccount.setFreezedAmount(bidAccount.getFreezedAmount().subtract(bid.getAmount()));
			// 生成一条取消投标金额流水
			accountFlowService.cancelBorrowFlow(bid, bidAccount);
		}

		// 统一更改投资人的账户
		for (Account a : bidAccountMap.values()) {
			accountService.update(a);
		}

	}

	@Override
	@Transactional(rollbackFor = { RuntimeException.class })
	public void audit2Audit(Long id, String remark, Integer state) {
		// 得到借款对象 判定对象是否为满标二审状态
		Borrow borrow = get(id);
		if (borrow != null && borrow.getState() == BidConst.BORROW_STATE_APPROVE_PENDING_2) {
			// 创建一个借款审核历史对象
			createBorrowAuditHistroy(state, borrow, remark, StatusConst.FULL_AUDIT2);

			// 审核成功
			if (state.equals(StatusConst.AUTH_PASS)) {
				// 1.针对审核人
				// 1.1修改借款状态(还款状态)
				borrow.setState(BidConst.BORROW_STATE_PAYING_BACK);
				// 1.2生成还款计划和收款计划
				List<RepaymentSchedule> rsList = createRepaymentSchedule(borrow);

				// 2.针对借款人
				// 2.1借款人收到借款 借款人账户余额增加 生成借款流水
				Account borrowAccount = accountService.get(borrow.getCreateUser().getId());

				borrowAccount.setUsableAmount(borrow.getAmount().add(borrowAccount.getUsableAmount()));
				accountFlowService.borrowFlow(borrow, borrowAccount);

				// 2.2借款人去掉借款状态
				UserInfo userInfo = userInfoService.get(borrow.getCreateUser().getId());
				userInfo.deleteState(BidStateUtil.OP_BORROW_PROCESS);
				userInfoService.update(userInfo);
				// 2.3借款人的信用额度减少
				borrowAccount.setRemainBorrowLimit(borrowAccount.getRemainBorrowLimit().subtract(borrow.getAmount()));
				// 2.4借款人的待还总额增加
				BigDecimal returnAmount = borrow.getAmount().add(borrow.getTotalInterestAmount());
				borrowAccount.setUnReturnAmount(borrowAccount.getUnReturnAmount().add(returnAmount));

				// 2.5支付平台手续费
				// 借款人账户余额减少生成支付借款手续费流水
				// 计算出手续费
				BigDecimal serviceCharge = CalculatetUtil.calAccountManagementCharge(borrow.getAmount());

				// 更新校验列 需要把金额精确到4位
				borrowAccount.setUsableAmount(DecimalFormatUtil.formatBigDecimal(
						borrowAccount.getUsableAmount().subtract(serviceCharge), BidConst.STORE_SCALE));

				// 更新借款人账户
				accountService.update(borrowAccount);
				accountFlowService.serviceChargeFlow(serviceCharge, borrowAccount, borrow);

				// 平台收取手续费(系统账户)，系统账户余额增加
				systemAccountService.chargeManager(borrow, serviceCharge);

				// 更新投资人账户
				updateInvestAccount(borrow, rsList);
			} else {
				// 审核失败
				cancelBorrow(borrow, BidConst.BORROW_STATE_REJECTED);
			}
			update(borrow);
			// 发送借款成功短信
			ac.publishEvent(new BorrowEvent(this, borrow));
		}

	}

	private void updateInvestAccount(Borrow borrow, List<RepaymentSchedule> rsList) {
		// 3.针对投资人 遍历所有投标
		Map<Long, Account> bidAccountMap = new HashMap<>(borrow.getBidList().size());
		// 3.1投资人冻结金额减少
		for (Bid b : borrow.getBidList()) {
			Long bidUserId = b.getCreateUser().getId();
			// 获取到当前投资人的账户
			Account bidAccount = bidAccountMap.get(bidUserId);
			// 生成成功投标流水
			if (bidAccount == null) {
				bidAccount = accountService.get(bidUserId);
				bidAccountMap.put(bidUserId, bidAccount);
			}
			bidAccount.setFreezedAmount(bidAccount.getFreezedAmount().subtract(b.getAmount()));
			accountFlowService.bidSuccessFlow(b, bidAccount);
		}
		// 3.2增加投资人总待收利息和待收本金
		for (RepaymentSchedule rs : rsList) {
			for (PaymentPlan pp : rs.getPaymentPlanList()) {
				// 得到投资人账户
				Account account = bidAccountMap.get(pp.getCollectLoginInfoId());
				// 增加投资人总待收利息和本金
				account.setUnReceiveInterest(account.getUnReceiveInterest().add(pp.getInterest()));
				account.setUnReceivePrincipal(account.getUnReceivePrincipal().add(pp.getPrincipal()));
			}
		}
		// 更新所有的投资人账户
		for (Account a : bidAccountMap.values()) {
			accountService.update(a);
		}
	}

	private List<RepaymentSchedule> createRepaymentSchedule(Borrow borrow) {
		Date nowDate = new Date();
		List<RepaymentSchedule> rsList = new ArrayList<>();
		// 前n-1期所有的本金
		BigDecimal totalPrincipal = BidConst.ZERO;
		// 前n-1期所有的利息
		BigDecimal totalInterest = BidConst.ZERO;
		// 遍历还款期数
		for (int i = 1; i <= borrow.getMonthReturn(); i++) {
			// 针对每一期还款创建一个还款计划对象
			RepaymentSchedule rs = new RepaymentSchedule();
			rs.setBorrowId(borrow.getId());
			rs.setBorrowTitle(borrow.getTitle());
			rs.setBorrowType(borrow.getType());
			rs.setBorrowUserId(borrow.getCreateUser().getId());
			rs.setMonthIndex(i);
			// 设置每一期的还款时间
			rs.setDeadline(DateUtils.addMonths(nowDate, i));
			rs.setState(RepaymentSchedule.NORMAL);
			rs.setReturnType(borrow.getReturnType());
			if (i < borrow.getMonthReturn()) {
				// 这一期的利息
				rs.setInterest(CalculatetUtil.calMonthlyInterest(borrow.getReturnType(), borrow.getAmount(),
						borrow.getRate(), i, borrow.getMonthReturn()));
				// 这一期的本息
				BigDecimal returnMoney = CalculatetUtil.calMonthlyReturnMoney(borrow.getReturnType(),
						borrow.getAmount(), borrow.getRate(), i, borrow.getMonthReturn());
				// 设置这一期的本金
				rs.setPrincipal(returnMoney.subtract(rs.getInterest()));
				rs.setTotalAmount(returnMoney);
				totalPrincipal = totalPrincipal.add(rs.getPrincipal());
				totalInterest = totalInterest.add(rs.getInterest());
			} else {
				// 一期的还款计划
				rs.setInterest(borrow.getTotalInterestAmount().subtract(totalInterest));
				rs.setPrincipal(borrow.getAmount().subtract(totalPrincipal));
				rs.setTotalAmount(rs.getInterest().add(rs.getPrincipal()));
			}
			repaymentScheduleService.saveAndUpdate(rs);
			rsList.add(rs);
			// 创建还款计划创建对应的收款计划
			createPaymentPlan(rs, borrow);
		}
		return rsList;
	}

	private void createPaymentPlan(RepaymentSchedule rs, Borrow borrow) {
		List<Bid> bidList = borrow.getBidList();
		// 前n-1个投的标所有的本金
		BigDecimal totalPrincipal = BidConst.ZERO;
		// 前n-1个投的标所有的利息
		BigDecimal totalInterest = BidConst.ZERO;
		// 遍历每一个投标
		for (Integer i = 0; i < bidList.size(); i++) {
			Bid bid = bidList.get(i);
			// 为每一个标创建回款计划对象
			PaymentPlan pp = new PaymentPlan();
			pp.setBidAmount(bid.getAmount());
			pp.setBidId(bid.getId());
			pp.setBorrowId(borrow.getId());
			pp.setDeadline(rs.getDeadline());
			pp.setReturnLoginInfoId(rs.getBorrowUserId());
			pp.setMonthIndex(rs.getMonthIndex());
			pp.setRepaymentSchedule(rs);
			pp.setReturnType(rs.getReturnType());
			pp.setCollectLoginInfoId(bid.getCreateUser().getId());
			if (i.equals((bidList.size() - 1))) {
				// 一个借款只有一个标的情况
				pp.setPrincipal(rs.getPrincipal().subtract(totalPrincipal));
				pp.setInterest(rs.getInterest().subtract(totalInterest));
				pp.setTotalAmount(pp.getPrincipal().add(pp.getInterest()));
			} else {
				// 计算投标占总借款的比率
				BigDecimal rate = bid.getAmount().divide(borrow.getAmount(), BidConst.CALC_SCALE, RoundingMode.HALF_UP);
				pp.setPrincipal(rs.getPrincipal().multiply(rate).setScale(BidConst.STORE_SCALE, RoundingMode.HALF_UP));
				pp.setInterest(rs.getInterest().multiply(rate).setScale(BidConst.STORE_SCALE, RoundingMode.HALF_UP));
				pp.setTotalAmount(pp.getPrincipal().add(pp.getInterest()));

				totalPrincipal = totalPrincipal.add(pp.getPrincipal());
				totalInterest = totalInterest.add(pp.getInterest());
			}

			paymentPlanService.saveAndUpdate(pp);
			rs.getPaymentPlanList().add(pp);
		}
	}

	@Override
	public Borrow get(Long id) {
		return repository.findOne(id);
	}

	@Override
	public List<BorrowAuditHistroy> getAuthHistroys(Long id) {
		return borrowAuditHistroyService.getByBorrowId(id);
	}

	@Override
	@Transactional(rollbackFor = { LogicException.class })
	public void update(Borrow borrow) {
		Borrow borrowUpdate = repository.saveAndFlush(borrow);
		if (borrowUpdate == null) {
			throw new LogicException("借款更新乐观锁异常");
		}
	}

	@Override
	public List<Borrow> listByState(int... state) {
		List<Borrow> borrowList = repository.findAll(new Specification<Borrow>() {
			@Override
			public Predicate toPredicate(Root<Borrow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				In<Integer> in = cb.in(root.get("state"));
				for (int i = 0; i < state.length; i++) {
					in.value(state[i]);
				}
				query.where(in);
				return null;
			}
		});
		return borrowList;
	}

	@Override
	public void getFailBorrow() {
		// 根据标的到期时间和招标状态将接下来一个小时内可能流标的标的Id和到期时间以到期时间的正序放到一个列表中缓存。
		Date date = new Date();
		Date addHours = DateUtils.addHours(date, 1);
		List<Borrow> list = repository.findAll(new Specification<Borrow>() {
			@Override
			public Predicate toPredicate(Root<Borrow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				list.add(cb.equal(root.get("state").as(Integer.class), BidConst.BORROW_STATE_BIDDING));
				list.add(cb.between(root.get("disableDate").as(Date.class), date, addHours));
				Predicate[] ps = new Predicate[list.size()];
				return cb.and(list.toArray(ps));
			}
		}, new Sort(Direction.ASC, "disableDate"));
		valueOperations.set("FailBorrowList", list);
	}

	@Override
	public void failBorrow() {
		// 拿到缓存列表的第一个标，判断到期时间是否小于当前时间，小于可能是流标，去数据库查询标的状态
		// 是招标中进行流标业务，从缓存列表去除。依次判断接下来的每一个标。
		List<Borrow> list = valueOperations.get("FailBorrowList");
		if (list != null) {
			Date date = new Date();
			for (Iterator<Borrow> iterator = list.iterator(); iterator.hasNext();) {
				Borrow borrow = iterator.next();
				if (borrow.getDisableDate().getTime() < date.getTime()) {
					Borrow newBorrow = get(borrow.getId());
					if (newBorrow.getState() == BidConst.BORROW_STATE_BIDDING) {
						// 流标操作
						cancelBorrow(newBorrow, BidConst.BORROW_STATE_BIDDING_OVERDUE);
						iterator.remove();
					}
				}
			}
			valueOperations.set("FailBorrowList", list);
		}
	}

}
