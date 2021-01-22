package com.wysoft.https_base.configuration;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.wysoft.https_base.util.JsonDateValueProcessor;

import net.sf.json.JsonConfig;

@Configuration
@PropertySource("classpath:appConfig.properties")
public class AppConfig {
	@Value("${isNeedAuth}")
	private boolean isNeedAuth;
	@Value("${service.protocol:http}")
	private String protocol;
	@Value("${service.host}")
	private String host;
	@Value("${service.port:9000}")
	private int port;
	@Value("${service.path:/httpServiceEntry}")
	private String httpServicePath;
	@Value("${service.ws.path:/services/webServiceEntry?wsdl}")
	private String webServicePath;
	@Value("${zookeeper.address}")
	private String zookeeperAddress;
	@Value("${zookeeper.timeout}")
	private int zookeeperTimeout;
	@Value("${zookeeper.root:/remoteServices}")
	private String zookeeperRoot;
	@Value("${remote.service.basePackage:com.wysoft}")
	private String basePackage;

	public boolean isNeedAuth() {
		return isNeedAuth;
	}

	public void setNeedAuth(boolean isNeedAuth) {
		this.isNeedAuth = isNeedAuth;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHttpServicePath() {
		return httpServicePath;
	}

	public void setHttpServicePath(String httpServicePath) {
		this.httpServicePath = httpServicePath;
	}

	public String getWebServicePath() {
		return webServicePath;
	}

	public void setWebServicePath(String webServicePath) {
		this.webServicePath = webServicePath;
	}

	public String getZookeeperAddress() {
		return zookeeperAddress;
	}

	public void setZookeeperAddress(String zookeeperAddress) {
		this.zookeeperAddress = zookeeperAddress;
	}

	public int getZookeeperTimeout() {
		return zookeeperTimeout;
	}

	public void setZookeeperTimeout(int zookeeperTimeout) {
		this.zookeeperTimeout = zookeeperTimeout;
	}

	public String getZookeeperRoot() {
		return zookeeperRoot;
	}

	public void setZookeeperRoot(String zookeeperRoot) {
		this.zookeeperRoot = zookeeperRoot;
	}

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	@Bean(name = "logger")
	public Logger logger() {
		return LogManager.getLogger("RollingFileServInfo");
	}

	@Bean(name = "jsonConfig")
	public JsonConfig jsonConfig() {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
		return jsonConfig;
	}
}
