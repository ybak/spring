package cn.qj.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.qj.core.entity.RepaymentSchedule;

/**
 * 收款计划
 * 
 * @author Qiujian
 * @date 2018/8/10
 */
public interface RepaymentScheduleRepository
		extends JpaRepository<RepaymentSchedule, Long>, JpaSpecificationExecutor<RepaymentSchedule> {

}
