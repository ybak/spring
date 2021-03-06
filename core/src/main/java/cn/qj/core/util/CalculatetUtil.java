package cn.qj.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.qj.core.consts.BidConst;

/**
 * 计算工具
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
public class CalculatetUtil {
	private CalculatetUtil() {

	}

	public static final BigDecimal ONE_HUNDRED = new BigDecimal("100.0000");
	public static final BigDecimal NUMBER_MONTHS_OF_YEAR = new BigDecimal("12.0000");

	/** 利息管理费费率 */
	public static final BigDecimal INTEREST_MANAGER_CHARGE_RATE = new BigDecimal("0.00");
	/** 借款管理费费率 */
	public static final BigDecimal ACCOUNT_MANAGER_CHARGE_RATE = new BigDecimal("0.03");

	/**
	 * 获取月利率
	 */
	public static BigDecimal getMonthlyRate(BigDecimal yearRate) {
		if (yearRate == null) {
			return BigDecimal.ZERO;
		}
		return yearRate.divide(ONE_HUNDRED).divide(NUMBER_MONTHS_OF_YEAR, BidConst.CALC_SCALE, RoundingMode.HALF_UP);
	}

	/**
	 * 计算借款总利息
	 *
	 * @param returnType
	 *            还款类型
	 * @param bidRequestAmount
	 *            借款金额
	 * @param yearRate
	 *            年利率
	 * @param monthes2Return
	 *            还款期限
	 * @return
	 */
	public static BigDecimal calTotalInterest(Integer returnType, BigDecimal bidRequestAmount, BigDecimal yearRate,
			Integer monthes2Return) {
		BigDecimal totalInterest = BigDecimal.ZERO;
		BigDecimal monthlyRate = getMonthlyRate(yearRate);
		// 按月分期
		if (BidConst.RETURN_TYPE_MONTH_INTEREST_PRINCIPAL.equals(returnType)) {
			// 只借款一个月
			if (monthes2Return.equals(1)) {
				totalInterest = bidRequestAmount.multiply(monthlyRate).setScale(BidConst.CALC_SCALE,
						RoundingMode.HALF_UP);
			} else {
				BigDecimal temp1 = bidRequestAmount.multiply(monthlyRate);
				BigDecimal temp2 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return);
				BigDecimal temp3 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return).subtract(BigDecimal.ONE);
				// 算出每月还款
				BigDecimal monthToReturnMoney = temp1.multiply(temp2).divide(temp3, BidConst.CALC_SCALE,
						RoundingMode.HALF_UP);
				// 算出总还款
				BigDecimal totalReturnMoney = monthToReturnMoney.multiply(new BigDecimal(monthes2Return));
				// 算出总利息
				totalInterest = totalReturnMoney.subtract(bidRequestAmount);
			}
			// 按月到期
		} else if (BidConst.RETURN_TYPE_MONTH_INTEREST.equals(returnType)) {
			BigDecimal monthlyInterest = DecimalFormatUtil.amountFormat(bidRequestAmount.multiply(monthlyRate));
			totalInterest = monthlyInterest.multiply(new BigDecimal(monthes2Return));
		}
		return DecimalFormatUtil.formatBigDecimal(totalInterest, BidConst.STORE_SCALE);
	}

	/**
	 * 计算每期利息
	 *
	 * @param returnType
	 *            还款类型
	 * @param bidRequestAmount
	 *            借款金额
	 * @param yearRate
	 *            年利率
	 * @param monthIndex
	 *            第几期
	 * @param monthes2Return
	 *            还款期限
	 * @return
	 */
	public static BigDecimal calMonthlyInterest(Integer returnType, BigDecimal bidRequestAmount, BigDecimal yearRate,
			Integer monthIndex, Integer monthes2Return) {
		BigDecimal monthlyInterest = BigDecimal.ZERO;
		BigDecimal monthlyRate = getMonthlyRate(yearRate);
		// 按月分期
		if (returnType.equals(BidConst.RETURN_TYPE_MONTH_INTEREST_PRINCIPAL)) {
			// 只借款一个月
			if (monthes2Return.equals(1)) {
				monthlyInterest = bidRequestAmount.multiply(monthlyRate).setScale(BidConst.CALC_SCALE,
						RoundingMode.HALF_UP);
			} else {
				BigDecimal temp1 = bidRequestAmount.multiply(monthlyRate);
				BigDecimal temp2 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return);
				BigDecimal temp3 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return).subtract(BigDecimal.ONE);
				BigDecimal temp4 = (BigDecimal.ONE.add(monthlyRate)).pow(monthIndex - 1);
				// 算出每月还款
				BigDecimal monthToReturnMoney = temp1.multiply(temp2).divide(temp3, BidConst.CALC_SCALE,
						RoundingMode.HALF_UP);
				// 算出总还款
				BigDecimal totalReturnMoney = monthToReturnMoney.multiply(new BigDecimal(monthes2Return));
				// 算出总利息
				BigDecimal totalInterest = totalReturnMoney.subtract(bidRequestAmount);

				if (monthIndex < monthes2Return) {
					monthlyInterest = (temp1.subtract(monthToReturnMoney)).multiply(temp4).add(monthToReturnMoney)
							.setScale(BidConst.CALC_SCALE, RoundingMode.HALF_UP);
				} else if (monthIndex.equals(monthes2Return)) {
					BigDecimal temp6 = BigDecimal.ZERO;
					// 汇总最后一期之前所有利息之和
					for (int i = 1; i < monthes2Return; i++) {
						BigDecimal temp5 = (BigDecimal.ONE.add(monthlyRate)).pow(i - 1);
						monthlyInterest = (temp1.subtract(monthToReturnMoney)).multiply(temp5).add(monthToReturnMoney)
								.setScale(BidConst.CALC_SCALE, RoundingMode.HALF_UP);
						temp6 = temp6.add(monthlyInterest);
					}
					monthlyInterest = totalInterest.subtract(temp6);
				}

			}
			// 按月到期
		} else if (returnType.equals(BidConst.RETURN_TYPE_MONTH_INTEREST)) {
			monthlyInterest = DecimalFormatUtil.amountFormat(bidRequestAmount.multiply(monthlyRate));
		}
		return monthlyInterest;
	}

	/**
	 * 计算每期还款
	 *
	 * @param returnType
	 *            还款类型
	 * @param bidRequestAmount
	 *            借款金额
	 * @param yearRate
	 *            年利率
	 * @param monthIndex
	 *            第几期
	 * @param monthes2Return
	 *            还款期限
	 * @return
	 */
	public static BigDecimal calMonthlyReturnMoney(Integer returnType, BigDecimal bidRequestAmount, BigDecimal yearRate,
			Integer monthIndex, Integer monthes2Return) {
		BigDecimal monthToReturnMoney = BigDecimal.ZERO;
		BigDecimal monthlyRate = getMonthlyRate(yearRate);
		// 按月分期
		if (returnType.equals(BidConst.RETURN_TYPE_MONTH_INTEREST_PRINCIPAL)) {
			if (monthes2Return.equals(1)) {
				monthToReturnMoney = bidRequestAmount.add(bidRequestAmount.multiply(monthlyRate))
						.setScale(BidConst.CALC_SCALE, RoundingMode.HALF_UP);
			} else {
				BigDecimal temp1 = bidRequestAmount.multiply(monthlyRate);
				BigDecimal temp2 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return);
				BigDecimal temp3 = (BigDecimal.ONE.add(monthlyRate)).pow(monthes2Return).subtract(BigDecimal.ONE);
				// 算出每月还款
				monthToReturnMoney = temp1.multiply(temp2).divide(temp3, BidConst.CALC_SCALE, RoundingMode.HALF_UP);
			}
			// 按月到期
		} else if (returnType.equals(BidConst.RETURN_TYPE_MONTH_INTEREST)) {
			BigDecimal monthlyInterest = bidRequestAmount.multiply(monthlyRate).setScale(BidConst.CALC_SCALE,
					RoundingMode.HALF_UP);
			if (monthIndex.equals(monthes2Return)) {
				monthToReturnMoney = bidRequestAmount.add(monthlyInterest).setScale(BidConst.CALC_SCALE,
						RoundingMode.HALF_UP);
			} else if (monthIndex < monthes2Return) {
				monthToReturnMoney = monthlyInterest;
			}
		}
		return DecimalFormatUtil.formatBigDecimal(monthToReturnMoney, BidConst.STORE_SCALE);
	}

	/**
	 * 计算一次投标实际获得的利息=投标金额/借款金额 *总利息
	 *
	 * @param bidRequestAmount
	 *            借款金额
	 * @param monthes2Return
	 *            还款期数
	 * @param yearRate
	 *            年利率
	 * @param returnType
	 *            还款类型
	 * @param acturalBidAmount
	 *            投标金额
	 * @return
	 */
	public static BigDecimal calBidInterest(BigDecimal bidRequestAmount, int monthes2Return, BigDecimal yearRate,
			int returnType, BigDecimal acturalBidAmount) {
		// 借款产生的总利息
		BigDecimal totalInterest = calTotalInterest(returnType, bidRequestAmount, yearRate, monthes2Return);
		// 所占比例
		BigDecimal proportion = acturalBidAmount.divide(bidRequestAmount, BidConst.CALC_SCALE, RoundingMode.HALF_UP);
		BigDecimal bidInterest = totalInterest.multiply(proportion);
		return DecimalFormatUtil.formatBigDecimal(bidInterest, BidConst.STORE_SCALE);
	}

	/**
	 * 计算利息管理费
	 * 
	 * @param interest
	 *            利息
	 * @param interestManagerChargeRate
	 *            利息管理费比例
	 * @return
	 */
	public static BigDecimal calInterestManagerCharge(BigDecimal interest) {
		return DecimalFormatUtil.formatBigDecimal(interest.multiply(INTEREST_MANAGER_CHARGE_RATE),
				BidConst.STORE_SCALE);
	}

	/**
	 * 计算借款管理费
	 *
	 * @param bidRequestAmount
	 *            借款金额
	 * @param returnType
	 *            还款类型
	 * @param monthes2Return
	 *            还款期限
	 * @return
	 */
	public static BigDecimal calAccountManagementCharge(BigDecimal bidRequestAmount) {
		BigDecimal accountManagementCharge = DecimalFormatUtil
				.formatBigDecimal(bidRequestAmount.multiply(ACCOUNT_MANAGER_CHARGE_RATE), BidConst.CALC_SCALE);
		return accountManagementCharge;
	}
}
