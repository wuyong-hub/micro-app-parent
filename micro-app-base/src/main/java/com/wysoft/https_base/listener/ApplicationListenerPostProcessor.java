package com.wysoft.https_base.listener;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import com.wysoft.https_base.annotation.RemoteServiceAnnotationProcessor;
import com.wysoft.https_base.configuration.AppConfig;

@Component
public class ApplicationListenerPostProcessor implements ApplicationListener<ApplicationEvent> {
	@Autowired
	private Logger logger;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private RemoteServiceAnnotationProcessor annotationProcessor;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextClosedEvent) {
			logger.info(event.getClass().getSimpleName() + " 事件已发生！");
		} else if (event instanceof ContextRefreshedEvent) {
			onContextRefreshedEvent((ContextRefreshedEvent) event);
		} else if (event instanceof ContextStartedEvent) {
			logger.info(event.getClass().getSimpleName() + " 事件已发生！");
		} else if (event instanceof ContextStoppedEvent) {
			logger.info(event.getClass().getSimpleName() + " 事件已发生！");
		} else if (event instanceof ApplicationStartedEvent) {
			logger.info(event.getClass().getSimpleName() + " 事件已发生！");
		} else if (event instanceof ApplicationReadyEvent) {
			logger.info(event.getClass().getSimpleName() + " 事件已发生！");
		} else {
			//logger.info("有其它事件发生:" + event.getClass().getName());
		}
	}

	public void onContextRefreshedEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() != null) {
			return;
		}
		logger.info("容器加载完成，执行后处理方法...");
		String basePackage = appConfig.getBasePackage();
		String[] packetNamees = basePackage.split(",");
		annotationProcessor.scanPacket(packetNamees);
	}

	@PreDestroy
	public void destory() {
		logger.info("应用被销毁!");
	}
}
