package com.wysoft.https_base.configuration;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wysoft.https_base.service.MyWebService;
import com.wysoft.https_base.service.MyWebServiceImpl;

@Configuration
public class CxfConfig {
	/**
	 * 指定Web Service发布到路径/services/*.
	 * @return ServletRegistrationBean
	 */
	@Bean("cxfServlet")
	public ServletRegistrationBean dispatcherServlet() {
		return new ServletRegistrationBean(new CXFServlet(), "/services/*");
	}

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}

	@Bean
	public MyWebService webService() {
		return new MyWebServiceImpl();
	}
	
	/**
	 * Web Service 入口地址指定.
	 * 地址：/services/webServiceEntry?wsdl
	 * @return Endpoint
	 */
	@Bean
	public Endpoint endpoint() {
		EndpointImpl endpoint = new EndpointImpl(springBus(), webService());
		endpoint.publish("/webServiceEntry");
		return endpoint;
	}
}
