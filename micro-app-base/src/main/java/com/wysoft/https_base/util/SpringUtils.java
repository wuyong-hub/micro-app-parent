package com.wysoft.https_base.util;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 
 * author:caoyuxiang 2019年8月19日 下午3:44:20
 */
@SuppressWarnings("unchecked")
@Component
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringUtils.applicationContext = applicationContext;
	}

	public static <T> T getBean(String beanName) {
		if (applicationContext.containsBean(beanName)) {
			return (T) applicationContext.getBean(beanName);
		} else {
			return null;
		}
	}

	public static <T> T getBean(Class<T> beanName) {

		return (T) applicationContext.getBean(beanName);
	}

	public static <T> Map<String, T> getBeansOfType(Class<T> baseType) {
		return applicationContext.getBeansOfType(baseType);
	}
}
