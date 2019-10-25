package cn.eeepay.framework.service.bill.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.eeepay.framework.dao.bill.RoleRigthMapper;
import cn.eeepay.framework.model.bill.RoleRigth;
import cn.eeepay.framework.model.bill.ShiroRigth;
import cn.eeepay.framework.service.bill.RoleRigthService;
import cn.eeepay.framework.service.bill.ShiroRigthService;
import cn.eeepay.framework.service.bill.UserRigthService;
@Service("roleRigthService")
@Transactional
public class RoleRigthServiceImpl implements RoleRigthService {
	@Resource
	public RoleRigthMapper roleRigthMapper;
	@Resource
	public ShiroRigthService shiroRigthService;
	@Resource
	public UserRigthService userRigthService;
	@Override
	public int insertRoleRigth(Integer roleId, Integer rigthId) throws Exception {
		return roleRigthMapper.insertRoleRigth(roleId, rigthId);
	}
	@Override
	public int deleteRoleRigth(Integer roleId, Integer rigthId) throws Exception {
		return roleRigthMapper.deleteRoleRigth(roleId, rigthId);
	}
	@Override
	public List<RoleRigth> findRoleRigthByRoleId(Integer roleId) throws Exception {
		return roleRigthMapper.findRoleRigthByRoleId(roleId);
	}
	@Override
	public int saveRoleRigth(Integer rId, Integer menuId, String[] rigthCodeArray) throws Exception {
		List<ShiroRigth> selectCheckBoxs = new ArrayList<>();
		List<ShiroRigth> shiroRigths = shiroRigthService.findShiroRigthByParentId(menuId);
		int i = 0;
		for (ShiroRigth shiroRigth : shiroRigths) {//清空原有的
			int n = this.deleteRoleRigth(rId, shiroRigth.getId());
			i += n;
		}
		for (ShiroRigth shiroRigth : shiroRigths) {
			for (int j = 0; j < rigthCodeArray.length; j++) {
				String _rigthCode = rigthCodeArray[j];
				if (shiroRigth.getRigthCode().equals(_rigthCode)) {
					selectCheckBoxs.add(shiroRigth);
					break;
				}
			}
		}
		for (ShiroRigth shiroRigth : selectCheckBoxs) {
			int n = this.insertRoleRigth(rId, shiroRigth.getId());
			int m = userRigthService.deleteUserRigthByRigthId(shiroRigth.getId());//同时删除权限对应(user_rigth)表的所有用户(userId)对应的权限(rigthId)
			
			i += n;
			i += m;
		}
		return i;
	}
	@Override
	public int saveAllRoleRigth(List<Map<String, String>> list) throws Exception {
		int i = 0;
		if (!list.isEmpty()) {
			String[] rightCodeArray = null;
			for (Map<String, String> item : list) {
				Integer rId = Integer.parseInt(item.get("roleId"));
				Integer menuId = Integer.parseInt(item.get("menuId"));
				rightCodeArray = item.get("rightCode").split(",");
				i = i + this.saveRoleRigth(rId, menuId, rightCodeArray);
			}
		}
		return i;
	}

	



	
}