package com.wysoft.https_auth.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wysoft.https_auth.interceptor.UserAuthInterceptor;

//@Configuration
public class UserAuthConfiguration implements WebMvcConfigurer{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//注册TestInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(new UserAuthInterceptor());
        registration.addPathPatterns("/**");                      //所有路径都被拦截
        registration.excludePathPatterns(                         //添加不拦截路径
                                         "/login",            //登录页
                                         "/qrCode",            //二维码授权
                                         "/authLogin",			 //二维码登录认证
                                         "/doLogin",            //登录认证
                                         "/httpServiceEntry",	//http服务入口
                                         "/webServiceEntry",	//web服务入口
                                         "/**/*.html",            //html静态资源
                                         "/**/*.js",              //js静态资源
                                         "/**/*.css",             //css静态资源
                                         "/**/*.png", 
                                         "/**/*.jpg", 
                                         "/**/*.gif", 
                                         "/**/*.woff",
                                         "/**/*.ttf"
                                         );    
	}
}
