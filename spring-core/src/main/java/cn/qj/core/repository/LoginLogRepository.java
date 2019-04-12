package cn.qj.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cn.qj.core.entity.LoginLog;

/**
 * 登录日志数据操作
 * 
 * @author Qiujian
 * @date 2019年3月26日
 *
 */
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

}
