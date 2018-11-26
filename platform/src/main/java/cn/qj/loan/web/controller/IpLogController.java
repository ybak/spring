package cn.qj.loan.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.qj.core.common.BaseResult;
import cn.qj.core.pojo.qo.IpLogQo;
import cn.qj.core.service.IpLogService;
import cn.qj.core.util.HttpServletContext;

/**
 * IP日志控制器
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Controller
@RequestMapping("/ipLog")
public class IpLogController {

	@Autowired
	private IpLogService service;

	@RequestMapping("/pageQuery")
	public String pageQueryList(IpLogQo ipLogQo, Model model) {
		ipLogQo.setIsLike(false);
		ipLogQo.setUsername(HttpServletContext.getCurrentLoginInfo().getUsername());
		model.addAttribute("pageResult", service.pageQueryIpLog(ipLogQo));
		model.addAttribute("ipLogQo", ipLogQo);
		return "ipLog_list";
	}

	@RequestMapping("/listAllVo")
	@ResponseBody
	public BaseResult listAllVo() {
		BaseResult result = new BaseResult();
		result.setMsg("查询成功");
		result.setStatusCode(200);
		result.setData(service.listAllVo());
		return result;
	}

	@RequestMapping("/page")
	@ResponseBody
	public BaseResult page() {
		BaseResult result = new BaseResult();
		result.setMsg("查询成功");
		result.setStatusCode(200);
		result.setData(service.page());
		return result;
	}

	@RequestMapping("/count")
	@ResponseBody
	public BaseResult count() {
		BaseResult result = new BaseResult();
		result.setMsg("统计成功");
		result.setStatusCode(200);
		result.setData(service.count());
		return result;
	}

}
