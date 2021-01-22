package com.wysoft.https_auth.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import com.wysoft.https_auth.dao.UserDao;
import com.wysoft.https_auth.model.UaamResource;
import com.wysoft.https_auth.model.UaamRole;
import com.wysoft.https_auth.model.UaamUser;

public class ShiroRealm extends AuthorizingRealm {
	@Autowired
	private UserDao userDao;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		 // 获取用户名
        String username = (String) principals.getPrimaryPrincipal();
        UaamUser user = userDao.findByUsername(username);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 给该用户设置角色，角色信息存在 t_role 表中取
        for (UaamRole role : user.getRoles()) {
            //添加角色
            simpleAuthorizationInfo.addRole(role.getRolename());
            //添加权限
            for (UaamResource permissions : role.getResources()) {
                simpleAuthorizationInfo.addStringPermission(permissions.getResname());
            }
        }
        return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		// 根据 Token 获取用户名，如果您不知道该 Token 怎么来的，先可以不管，下文会解释
        String username = (String) token.getPrincipal();
        // 根据用户名从数据库中查询该用户
        UaamUser user = userDao.findByUsername(username);
        if(user != null) {
            // 把当前用户存到 Session 中
            SecurityUtils.getSubject().getSession().setAttribute("user", user);
            // 传入用户名和密码进行身份认证，并返回认证信息
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getUsername(), user.getPassword(), "myRealm");
            return authcInfo;
        } else {
            return null;
        }
	}
	
}
