package cn.qj.loan.config.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import cn.qj.core.consts.SysConst;
import cn.qj.core.entity.IpLog;
import cn.qj.core.entity.LoginInfo;
import cn.qj.core.service.IpLogService;
import cn.qj.core.service.LoginInfoService;

/**
 * 自定义登录失败处理
 * 
 * @author Qiujian
 *
 */
@Component
public class LoanLoginFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private IpLogService ipLogService;
	@Autowired
	private LoginInfoService loginInfoService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		if (exception instanceof BadCredentialsException) {
			String username = request.getParameter(SysConst.USERNAME_STR);
			LoginInfo loginInfo = loginInfoService.getLoginInfoByUsername(username);
			loginInfo.setLoserCount(loginInfo.getLoserCount() + 1);
			
			Date currentDate = new Date();
			if (loginInfo.getLoserCount().equals(LoginInfo.LOSER_MAX_COUNT)) {
				// 达到次数进行锁定
				loginInfo.setStatus(LoginInfo.LOCK);
				loginInfo.setLockTime(currentDate);
			}
			// 登录日志记录
			IpLog ipLog = new IpLog();
			ipLog.setIp(request.getRemoteAddr());
			ipLog.setUsername(username);
			ipLog.setUserType(LoginInfo.MANAGER);
			ipLog.setLoginTime(currentDate);
			ipLog.setLoginState(IpLog.LOGIN_FAIL);
			ipLog.setGmtCreate(currentDate);
			ipLog.setGmtModified(currentDate);
			loginInfoService.saveLoginInfo(loginInfo);
			ipLogService.saveIpLog(ipLog);
		}
		request.setAttribute("msg", exception.getMessage());
		RequestDispatcher requestDispatcher = request.getRequestDispatcher(SysConst.URL_LOGIN_INFO_AJAX);
		requestDispatcher.forward(request, response);
	}

}