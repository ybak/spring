package cn.qj.loan.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.qj.core.common.BaseResult;
import cn.qj.core.entity.Recharge;
import cn.qj.core.pojo.qo.RechargeQo;
import cn.qj.core.service.CompanyBankInfoService;
import cn.qj.core.service.RechargeService;
import cn.qj.core.util.HttpSessionUtil;

/**
 * 充值控制器
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Controller
public class RechargeController {

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
		return "recharge";
	}

	/**
	 * 提交线下充值单
	 */
	@RequestMapping("/recharge/save")
	@ResponseBody
	public BaseResult save(Recharge recharge) {
		BaseResult result = new BaseResult();
		service.apply(recharge);
		result.setSuccess(true);
		return result;
	}

	/**
	 * 线下充值列表
	 */
	@RequestMapping("/recharge/list")
	public String list(@ModelAttribute("qo") RechargeQo qo, Model model) {
		qo.setApplierId(HttpSessionUtil.getCurrentLoginInfo().getId());
		model.addAttribute("pageResult", service.list(qo));
		return "recharge_list";
	}
}
