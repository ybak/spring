package cn.eeepay.framework.enums;

public enum DuiAccountStatus {
	
	/**
	 * 核对成功
	 * */
	SUCCESS,
	
	/**
	 * 核对有误
	 * */
	FAILED,
	
	/**
	 * 平台单边
	 * */
	PLATE_SINGLE,
	
	/**
	 * 收单机构单边
	 * */
	ACQ_SINGLE,
	
	/**
	 * 金额不符
	 * */
	AMOUNT_FAILED,
	
	/**
	 * 未核对
	 * */
	NO_CHECKED;

}
