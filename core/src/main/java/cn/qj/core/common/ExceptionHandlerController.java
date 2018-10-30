package cn.qj.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.qj.core.pojo.vo.AjaxResult;

/**
 * 系统异常处理
 * 
 * @author Qiujian
 * @date 2018/10/30
 */
@RestControllerAdvice
public class ExceptionHandlerController {

	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(LogicException.class)
	public AjaxResult handlerLogicException(LogicException e) {
		logger.error("逻辑异常", e);
		return new AjaxResult(false, e.getMessage(), e.getErrCode());
	}

	@ExceptionHandler(Exception.class)
	public AjaxResult handlerException(Exception e) {
		logger.error("系统异常", e);
		return new AjaxResult(false, "系统异常", 500);
	}

}