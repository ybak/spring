package cn.qj.core.pojo.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机验证码
 * 
 * @author Qiujian
 * @date 2018/11/01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyCode {
	private String phoneNumber;
	private String verifyCode;
	private Date date;
}
