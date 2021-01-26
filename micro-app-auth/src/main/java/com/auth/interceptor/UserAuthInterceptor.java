package com.auth.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wysoft.https_auth.model.UaamUser;

@Component
public class UserAuthInterceptor implements HandlerInterceptor {
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		try {
			// 统一拦截（查询当前session是否存在user）(这里user会在每次登陆成功后，写入session)
			UaamUser user = (UaamUser) request.getSession().getAttribute("session_user");
			if (user != null) {
				return true;
			}
			String path = request.getRequestURI();
			response.sendRedirect(request.getContextPath() + "login?redirectPath=" + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;// 如果设置为false时，被请求时，拦截器执行到此处将不会继续操作
						// 如果设置为true时，请求将会继续执行后面的操作
	}

}
