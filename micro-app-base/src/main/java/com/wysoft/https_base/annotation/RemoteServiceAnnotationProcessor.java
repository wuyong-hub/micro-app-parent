package com.wysoft.https_base.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.configuration.AppConfig;
import com.wysoft.https_base.util.HttpUtils;
import com.wysoft.https_base.zookeeper.ZkApi;

@Component
public class RemoteServiceAnnotationProcessor extends AbstractAnnotationProcessor{
	@Autowired
	private Logger logger;
	@Autowired
	private ZkApi zkApi;
	@Autowired
	private AppConfig appConfig;
	
	class RemoteService{
		String serviceId;
		List<RemoteServiceAction> actions = new ArrayList<>();

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public List<RemoteServiceAction> getActions() {
			return actions;
		}

		public void setActions(List<RemoteServiceAction> actions) {
			this.actions = actions;
		}

		@Override
		public String toString() {
			String servStr = "";
			servStr += "[" + serviceId + "]\n";
			for(RemoteServiceAction action : actions) {
				servStr += action;
			}
			return servStr;
		}

	}
	
	class RemoteServiceAction{
		String name;
		Class returnType;
		Class[] paraTypes;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Class getReturnType() {
			return returnType;
		}
		public void setReturnType(Class returnType) {
			this.returnType = returnType;
		}
		public Class[] getParaTypes() {
			return paraTypes;
		}
		public void setParaTypes(Class[] paraTypes) {
			this.paraTypes = paraTypes;
		}
		@Override
		public String toString() {
			String actionStr = "";
			actionStr += "public " + returnType.getName() + " " + name + "(";
			for(Class cls : paraTypes) {
				actionStr += cls.getName() + ",";
			}
			if(paraTypes != null && paraTypes.length > 0) {
				actionStr = actionStr.substring(0,actionStr.lastIndexOf(","));
			}
			actionStr += ")";
			return actionStr;
		}
	}
	
	@Override
	public void processClass(Class<?> klass) {
		if(klass.isAnnotationPresent(Service.class)) {
			Service annotation = klass.getAnnotation(Service.class);
			RemoteService rService = new RemoteService();
			String serviceId = annotation.value();
			if(StringUtils.isEmpty(serviceId)) {
				return;
			}
			rService.setServiceId(serviceId);
			Method[] methods = klass.getDeclaredMethods();
			for(Method method : methods) {
				if(method.isAnnotationPresent(RemoteMethod.class)) {
					RemoteServiceAction action = new RemoteServiceAction();
					RemoteMethod rmAnnotation = method.getAnnotation(RemoteMethod.class);
					String name = rmAnnotation.value();
					if(StringUtils.isEmpty(name)) {
						name = method.getName();
					}
					action.setName(name);
					action.setReturnType(method.getReturnType());
					action.setParaTypes(method.getParameterTypes());
					rService.getActions().add(action);
				}
			}
			registerService(rService); 
		}
	}
	
	private void registerService(RemoteService remoteService) {
		List<RemoteServiceAction> actions = remoteService.getActions();
		if(actions == null || actions.size() <= 0) {
			return ;
		}
		logger.info("注册分布式服务:[" + remoteService.getServiceId() + "]");
		String root = appConfig.getZookeeperRoot();
		String servicePath = root + "/" + remoteService.getServiceId();
		if(!zkApi.exists(root, false)) {
			zkApi.createNode(root, "", CreateMode.PERSISTENT);
		}
		if(!zkApi.exists(servicePath, false)) {
			zkApi.createNode(servicePath, "", CreateMode.PERSISTENT);
		}
		for(RemoteServiceAction action:remoteService.getActions()) {
			String actionPath = servicePath + "/" + action.getName();
			if(!zkApi.exists(actionPath, false)) {
				zkApi.createNode(actionPath, action.toString(), CreateMode.PERSISTENT);
			}
			
			String httpServiceEntry = "";
			httpServiceEntry += appConfig.getProtocol() + "://"
					+ (StringUtils.isNotEmpty(appConfig.getHost()) ? appConfig.getHost() : HttpUtils.getLocalHost())
					+ ":" + appConfig.getPort()
					+ appConfig.getHttpServicePath();
			
			if(zkApi.createNode(actionPath + "/provider", httpServiceEntry, CreateMode.EPHEMERAL_SEQUENTIAL)) {
				logger.info(String.format("已创建服务节点[%s:%s]",actionPath, httpServiceEntry));
			}
		}
	}
	
	public static void main(String[] args) {
		/*
		AbstractAnnotationProcessor annotationProcessor = new AbstractAnnotationProcessor() {
			
			@Override
			public void processClass(Class<?> klass) {
				System.out.println(klass);
			}
		};
		annotationProcessor.scanPacket("com.wysoft.https_base");
		*/
		
		RemoteServiceAnnotationProcessor annotationProcessor = new RemoteServiceAnnotationProcessor();
		annotationProcessor.scanPacket("com.wysoft.https_base");
	}
}
