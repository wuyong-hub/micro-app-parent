package com.wysoft.https_base.service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.dao.JdbcDao;
import com.wysoft.https_base.util.Base64Utils;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.MD5;

import net.sf.json.JSONObject;

@Service
public class AuthVerifyService {
	@Autowired
	private JdbcDao jdbcDao;
	
	public JSONObject verify(String authCode,String clientIp) {
		JSONObject result = JSONUtil.getResult("认证成功！");
		
		if (StringUtils.isEmpty(authCode)) {
			result = JSONUtil.getErrMsg("授权码【authCode】不能为空！");
			return result;
		}

		// 授予信息认证
		String authCodeStr = Base64Utils.decode(authCode);
		String[] authArray = authCodeStr.split("\\|");
		if (authArray == null || authArray.length < 3) {
			result = JSONUtil.getErrMsg("授权码有误!");
			return result;
		}
		String orgInfo = authArray[0], uid = authArray[1], passwd = authArray[2];
		Map<String, Object> authAccessMap = jdbcDao.queryForMap("select * from uaam_auth_access t where t.org_info=? and t.uid=?", orgInfo, uid);
		if (authAccessMap != null && authAccessMap.size() > 0) {
			String originPasswd = authAccessMap.get("passwd").toString();
			if (!MD5.encodeByMD5(passwd).equals(originPasswd)) {
				result = JSONUtil.getErrMsg("授权信息密码不正确!");
				return result;
			}
		} else {
			result = JSONUtil.getErrMsg("授权验证不通过!");
			return result;
		}

		if (authAccessMap.get("ip_pattern") != null) {
			Pattern p = Pattern.compile(authAccessMap.get("ip_pattern").toString());
			Matcher match = p.matcher(clientIp);
			if (!match.find()) {
				result = JSONUtil.getErrMsg("IP【\" + clientIp + \"】未被授权");
				return result;
			}
		}
		return result;
	}
}
