package cn.qj.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.qj.core.entity.LoginLog;
import cn.qj.core.repository.LoginLogRepository;

/**
 * 登录日志服务
 * 
 * @author Qiujian
 * @date 2019年3月26日
 *
 */
@Service
public class LoginLogService {

	@Autowired
	private LoginLogRepository loginLogRepository;

	@Transactional(rollbackFor = RuntimeException.class)
	public void save(LoginLog log) {
		loginLogRepository.save(log);
	}

}
