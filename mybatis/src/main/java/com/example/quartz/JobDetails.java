package com.example.quartz;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 任务
 * 
 * @author Qiu Jian
 *
 */
@Getter
@Setter
@ToString
public class JobDetails implements Serializable {
	private static final long serialVersionUID = 5920419573204731379L;

	public static final int JOB_TYPE_CRON = 0;
	public static final int JOB_TYPE_SIMPLE = 1;

	private String jobGroupName;
	private String jobName;
	private String jobDescription;
	private String jobClass;
	private String triggerState;
	private String triggerDescription;
	private Date triggerStartTime;
	private Date triggerEndTime;
	/**
	 * 下次触发时间
	 */
	private Date triggerNextFireTime;
	/**
	 * 上次触发时间
	 */
	private Date triggerPreviousFireTime;
	/**
	 * 最后触发时间
	 */
	private Date triggerFinalFireTime;
	private String cronExpression;
	/**
	 * 重复次数
	 */
	private Integer repeatCount;
	/**
	 * 间隔时间
	 */
	private Long repeatInterval;
	/**
	 * 总触发次数
	 */
	private Integer triggeredTimes;

	private Integer triggerType;

}
