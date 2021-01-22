package com.wysoft.https_auth.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wysoft.https_auth.dao.UserDao;
import com.wysoft.https_auth.model.UaamUser;
import com.wysoft.https_base.util.MD5;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserDao userDao;
	
	/**
     * 身份认证测试接口
     * @param request
     * @return
     */
    @RequestMapping("/admin")
    public String admin(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("user");
        return "ehrview";
    }

    /**
     * 角色认证测试接口
     * @param request
     * @return
     */
    @RequestMapping("/student")
    public String student(HttpServletRequest request) {
        return "success";
    }

    /**
     * 权限认证测试接口
     * @param request
     * @return
     */
    @RequestMapping("/teacher")
    public String teacher(HttpServletRequest request) {
        return "success";
    }
    
    /**
     * 用户登录接口
     * @param user user
     * @param request request
     * @return string
     */
    @RequestMapping("/login")
    @ResponseBody
    public JSONObject login(String username,String password, HttpServletRequest request) {
    	JSONObject json = new JSONObject();
    	json.put("code", 200);
    	json.put("msg", "登录成功！");
    	
    	UaamUser user = userDao.findByUsername(username);
        // 根据用户名和密码创建 Token
        UsernamePasswordToken token = new UsernamePasswordToken(username, MD5.encodeByMD5(password));
        // 获取 subject 认证主体
        Subject subject = SecurityUtils.getSubject();
        try{
            // 开始认证，这一步会跳到我们自定义的 Realm 中
            subject.login(token);
            request.getSession().setAttribute("user", user);
            return json;
        }catch(Exception e){
            e.printStackTrace();
            request.getSession().setAttribute("user", user);
            request.setAttribute("error", "用户名或密码错误！");
            json.put("code", 501);
        	json.put("msg", "用户名或密码错误！");
        	return json;
        }
    }
}
