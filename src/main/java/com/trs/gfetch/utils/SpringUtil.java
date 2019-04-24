package com.trs.gfetch.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtil implements ApplicationContextAware {

	public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	//spring中获得bean
	public static Object getObject(String beanId){
		return getApplicationContext().getBean(beanId);
	}
	/**
	 * 通过class获取Bean.
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}
	/**
	 * 获取applicationContext
	 * @return
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}
}
