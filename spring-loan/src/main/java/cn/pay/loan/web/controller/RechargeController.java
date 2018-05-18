package cn.pay.loan.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.pay.core.domain.business.Recharge;
import cn.pay.core.obj.qo.RechargeQo;
import cn.pay.core.obj.vo.AjaxResult;
import cn.pay.core.service.CompanyBankInfoService;
import cn.pay.core.service.RechargeService;
import cn.pay.core.util.HttpSessionContext;

/**
 * 充值相关
 * 
 * @author Administrator
 *
 */
@Controller
public class RechargeController {
	public static final String RECHARGE = "recharge";
	public static final String RECHARGE_LIST = "recharge_list";

	@Autowired
	private CompanyBankInfoService companyBankInfoService;
	@Autowired
	private RechargeService service;

	/**
	 * 导航到线下充值界面
	 */
	@RequestMapping("/recharge")
	public String recharge(Model model) {
		// 查询出所有的银行账户信息
		model.addAttribute("banks", companyBankInfoService.list());
		return RECHARGE;
	}

	/**
	 * 提交线下充值单
	 */
	@RequestMapping("/recharge/save")
	@ResponseBody
	public AjaxResult save(Recharge recharge) {
		AjaxResult result = new AjaxResult();
		service.apply(recharge);
		result.setSuccess(true);
		return result;
	}

	/**
	 * 线下充值列表
	 */
	@RequestMapping("/recharge/list")
	public String list(@ModelAttribute("qo") RechargeQo qo, Model model) {
		qo.setApplierId(HttpSessionContext.getCurrentLoginInfo().getId());
		model.addAttribute("pageResult", service.list(qo));
		return RECHARGE_LIST;
	}
}
