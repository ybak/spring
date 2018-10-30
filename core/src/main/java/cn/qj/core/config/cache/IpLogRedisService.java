package cn.qj.core.config.cache;

import org.springframework.stereotype.Service;

import cn.qj.core.entity.IpLog;

/**
 * 用户登录历史Redis服务
 * 
 * @author Administrator
 *
 */
@Service
public class IpLogRedisService extends AbstractRedisService<IpLog> {

	private static final String IP_LOG_KEY = "IP_LOG_KEY";

	@Override
	protected String getRedisKey() {
		return IP_LOG_KEY;
	}

}