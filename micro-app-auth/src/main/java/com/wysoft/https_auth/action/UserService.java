package com.wysoft.https_auth.action;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysoft.https_auth.dao.AuthLoginDao;
import com.wysoft.https_auth.dao.UserDao;
import com.wysoft.https_auth.model.UaamAuthLogin;
import com.wysoft.https_auth.model.UaamUser;
import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.MD5;

import net.sf.json.JSONObject;

@Service("userAuth")
public class UserService extends BaseService {
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuthLoginDao authLoginDao;

	/**
	 * 用户认证.
	 */
	@Override
	@RemoteMethod
	public JSONObject doAction(JSONObject json) {
		String username = JSONUtil.getString(json, "username");
		String password = JSONUtil.getString(json, "password");
		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			return JSONUtil.getErrMsg("用户名或密码不能为空！");
		}

		UaamUser user = userDao.findByUsername(username);
		if (user == null) {
			return JSONUtil.getErrMsg("用户不存在！");
		}
		if (!MD5.encodeByMD5(password).equals(user.getPassword())) {
			return JSONUtil.getErrMsg("密码错误！");
		}

		JSONObject result = JSONUtil.getResult();
		result.put("data", user);
		return result;
	}
	
	/**
	 * 用户注册.
	 * @param json
	 * @return
	 */
	@RemoteMethod
	public JSONObject register(JSONObject json) {
		String username = JSONUtil.getString(json, "username");
		String password = JSONUtil.getString(json, "password");
		UaamUser user = userDao.findByUsername(username);
		if (user != null) {
			return JSONUtil.getErrMsg("用户已存在！");
		}
		
		user = new UaamUser();
		user.setUsername(username);
		user.setPassword(MD5.encodeByMD5(password));
		user.setStatus("0");
		
		user = userDao.save(user);
		JSONObject result = JSONUtil.getResult();
		result.put("data", user);
		return result;
	}
	
	public void saveAuthLogin(UaamAuthLogin authLogin) {
		authLoginDao.save(authLogin);
	}
	
	public JSONObject getAuthLogin(String authuuid) {
		JSONObject result = JSONUtil.getResult();
		Optional<UaamAuthLogin> option = authLoginDao.findById(authuuid);
		if(option.isPresent()) {
			UaamAuthLogin authLogin = option.get();
			result.put("data", authLogin);
			if(authLogin.getLoginstatus() == 1) {
				authLoginDao.delete(authLogin);
				return result;
			}
			Date d = authLogin.getAuthtime();
			d = DateUtils.addMinutes(d, 3);
			
			//已过了三分钟,二维码失效
			if(d.compareTo(new Date()) < 0) {
				authLoginDao.delete(authLogin);
				result = JSONUtil.getErrMsg("二维码失效,请刷新！");
				result.put("code", 401);	//二维码失效代码
			}
		}
		else {
			result = JSONUtil.getErrMsg("认证信息不存在！");
		}
		return result;
	}
	
	@RemoteMethod
	public JSONObject setAuthLogin(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		
		String uuid = JSONUtil.getString(json, "uuid");
		//String appid = JSONUtil.getString(json, "appid");
		String username = JSONUtil.getString(json, "username");
		String password = JSONUtil.getString(json, "password");
		
		Optional<UaamAuthLogin> option = authLoginDao.findById(uuid);
		
		if(option.isPresent()) {
			UaamAuthLogin authLogin = option.get();
			authLogin.setUsername(username);
			authLogin.setPassword(password);
			authLogin.setLoginstatus(1);
			
			authLoginDao.save(authLogin);
		}
		
		else {
			result = JSONUtil.getErrMsg("授权信息不存在！");
		}
		
		return result;
	}
	
	@RemoteMethod
	public JSONObject shiroLogin(JSONObject json) {
		JSONObject result = JSONUtil.getResult();

		String username = JSONUtil.getString(json, "username");
		String password = JSONUtil.getString(json, "password");
		
		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			result = JSONUtil.getErrMsg("用户名或密码不能为空!");
			return result;
		}
		
    	UaamUser user = userDao.findByUsername(username);
        // 根据用户名和密码创建 Token
        UsernamePasswordToken token = new UsernamePasswordToken(username, MD5.encodeByMD5(password));
        // 获取 subject 认证主体
        Subject subject = SecurityUtils.getSubject();
        try{
            // 开始认证，这一步会跳到我们自定义的 Realm 中
            subject.login(token);
            String sessionId = (String) subject.getSession().getId();
            JSONObject data = new JSONObject();
            data.put("token",sessionId);
            data.put("user", user);
            result.put("data", data);
            return result;
        }catch(Exception e){
            e.printStackTrace();
            result = JSONUtil.getErrMsg("用户名或密码错误！");
        	return result;
        }
	}
}
