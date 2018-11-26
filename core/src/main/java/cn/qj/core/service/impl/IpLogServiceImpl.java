package cn.qj.core.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import cn.qj.core.common.DataSourceKey;
import cn.qj.core.common.PageResult;
import cn.qj.core.entity.IpLog;
import cn.qj.core.pojo.qo.IpLogQo;
import cn.qj.core.pojo.vo.IpLogCountVo;
import cn.qj.core.pojo.vo.IpLogVo;
import cn.qj.core.repository.IpLogRepository;
import cn.qj.core.service.IpLogService;
import cn.qj.core.util.DataSourceUtil;
import cn.qj.core.util.ResultUtil;

/**
 * 登录日志服务实现
 * 
 * @author Qiujian
 * @date 2018/8/10
 */
@Service
public class IpLogServiceImpl implements IpLogService {

	@Autowired
	private IpLogRepository repository;

	@Autowired
	private EntityManager entityManager;

	@Override
	@Cacheable("pageQueryIpLog")
	@DataSourceKey(DataSourceUtil.READ_ONE_KEY)
	public PageResult pageQueryIpLog(IpLogQo qo) {
		Page<IpLog> page = repository.findAll(new Specification<IpLog>() {
			@Override
			public Predicate toPredicate(Root<IpLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<>();
				if (qo.getState() != -1) {
					Predicate loginState = cb.equal(root.get("loginState").as(Integer.class), qo.getState());
					predicateList.add(loginState);
				}
				if (qo.getBeginDate() != null) {
					Predicate beginLoginTime = cb.greaterThanOrEqualTo(root.get("loginTime").as(Date.class),
							qo.getBeginDate());
					predicateList.add(beginLoginTime);
				}
				if (qo.getEndDate() != null) {
					Predicate endLoginTime = cb.lessThanOrEqualTo(root.get("loginTime").as(Date.class),
							qo.getEndDate());
					predicateList.add(endLoginTime);
				}
				if (StringUtils.hasLength(qo.getUsername())) {
					if (qo.getIsLike()) {
						Predicate likeUsername = cb.like(root.get("username"), qo.getUsername() + "%");
						predicateList.add(likeUsername);
					} else {
						Predicate equalUsername = cb.equal(root.get("username"), qo.getUsername());
						predicateList.add(equalUsername);
					}
				}
				Predicate[] predicateArray = new Predicate[predicateList.size()];
				return cb.and(predicateList.toArray(predicateArray));
			}
		}, new PageRequest(qo.getCurrentPage() - 1, qo.getPageSize(), Direction.DESC, "loginTime"));
		if (page.getTotalElements() < 1) {
			return ResultUtil.empty(qo.getPageSize());
		}
		return new PageResult(page.getContent(), page.getTotalPages(), qo.getCurrentPage(), qo.getPageSize());
	}

	@Override
	@Cacheable("getNewestIpLogByUsername")
	public IpLog getNewestIpLogByUsername(String username) {
		return repository.findByUsernameOrderByLoginTimeDesc(username, new PageRequest(0, 1)).get(0);
	}

	@CacheEvict(value = { "pageQueryIpLog", "getNewestIpLogByUsername" }, allEntries = true)
	@Transactional(rollbackFor = { RuntimeException.class })
	@Override
	public IpLog saveIpLog(IpLog ipLog) {
		return repository.saveAndFlush(ipLog);
	}

	@CacheEvict(value = { "pageQueryIpLog", "getNewestIpLogByUsername" }, allEntries = true)
	@Transactional(rollbackFor = { RuntimeException.class })
	@Override
	public IpLog updateIpLog(IpLog ipLog) {
		return repository.saveAndFlush(ipLog);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IpLogVo> listAllVo() {
		String querySql = "SELECT i_l.ip AS ip,i_l.username AS username FROM ip_log i_l WHERE i_l.username=?1 ";
		Query nativeQuery = entityManager.createNativeQuery(querySql);
		nativeQuery.setParameter(1, "独孤求败");
		org.hibernate.Query query = nativeQuery.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.aliasToBean(IpLogVo.class));
		return query.list();
	}

	@Override
	public Page<IpLog> page() {
		return repository.findAll(new PageRequest(1, 10, Direction.DESC, "loginTime"));
	}

	@Override
	public IpLogCountVo count() {
		String querySql = "SELECT count(i_l.id) AS count,i_l.username AS username FROM ip_log i_l WHERE i_l.username = ?1 GROUP BY username";
		Query nativeQuery = entityManager.createNativeQuery(querySql);
		nativeQuery.setParameter(1, "独孤求败111");
		org.hibernate.Query query = nativeQuery.unwrap(SQLQuery.class)
				.setResultTransformer(Transformers.aliasToBean(IpLogCountVo.class));
		return (IpLogCountVo) query.uniqueResult();
	}

}
