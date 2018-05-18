package cn.pay.core.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pay.core.dao.UserInfoRepository;
import cn.pay.core.domain.business.UserInfo;
import cn.pay.core.obj.vo.VerifyCode;
import cn.pay.core.service.UserInfoService;
import cn.pay.core.util.BidStateUtil;
import cn.pay.core.util.DateUtil;
import cn.pay.core.util.HttpSessionContext;
import cn.pay.core.util.LogicException;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoRepository repository;

	@Override
	public UserInfo get(Long id) {
		return repository.findOne(id);
	}

	@Override
	@Transactional
	public void saveBasicInfo(UserInfo userInfo) {
		// 拿到当前用户基本资料
		UserInfo info = repository.findOne(HttpSessionContext.getCurrentLoginInfo().getId());
		// 设置只需要修改的内容 明细信息
		info.setEducationBackground(userInfo.getEducationBackground());
		info.setHouseCondition(userInfo.getHouseCondition());
		info.setIncomeGrade(userInfo.getIncomeGrade());
		info.setKidCount(userInfo.getKidCount());
		info.setMarriage(userInfo.getMarriage());
		if (!info.getIsBasicInfo()) {
			info.addState(BidStateUtil.OP_BASIC_INFO);
		}
		// 视频认证
		if (!info.getIsVedioAuth()) {
			info.addState(BidStateUtil.OP_VEDIO_AUTH);
		}
		update(info);
	}

	@Override
	@Transactional
	public void bind(String phoneNumber, String verifyCode) {
		// 如果当前用户已经绑定手机,直接略过
		UserInfo userInfo = get(HttpSessionContext.getCurrentLoginInfo().getId());
		if (!userInfo.getIsBindPhone()) {
			// 检查验证码手机号，验证有效期
			boolean ret = checkVerifyCode(phoneNumber, verifyCode);
			if (ret) {
				// 绑定手机 修改状态码
				userInfo.setPhoneNumber(phoneNumber);
				userInfo.addState(BidStateUtil.OP_BIND_PHONE);
				update(userInfo);
			} else {
				throw new LogicException("绑定失败");
			}
		}
	}

	private boolean checkVerifyCode(String phoneNumber, String verifyCode) {
		VerifyCode vc = HttpSessionContext.getVerifyCode();
		if (vc != null && vc.getPhoneNumber().equals(phoneNumber) && verifyCode.equals(verifyCode)
				&& DateUtil.setBetweenDate(new Date(), vc.getDate()) < 180) {
			return true;
		}
		return false;
	}

	@Override
	@Transactional
	public void update(UserInfo userInfo) {
		UserInfo ui = repository.saveAndFlush(userInfo);
		if (ui == null) {
			throw new LogicException("用户信息更新乐观锁异常");
		}
	}

	@Override
	@Transactional
	public void save(UserInfo userInfo) {
		repository.saveAndFlush(userInfo);
	}

}
