package com.wysoft.https_base.service;

import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;

import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.XmlTool;

import net.sf.json.JSONObject;

/**
 * Web Service入口类.
 * @author Wuyong
 *
 */
@WebService(name = "webService", targetNamespace = "http://service.wysoft.com", endpointInterface = "com.wysoft.https_base.service.MyWebService")
public class MyWebServiceImpl implements MyWebService {
	@Autowired
	private Logger log;
	
	@Autowired
	HttpService httpService;
	
	@Resource
	private WebServiceContext wsContext;
	
	/**
	 * web服务入口方法.
	 * @param body XML输入串
	 * @return xml结果
	 */
	@Override
	public String service(String body) {
		MessageContext mc = wsContext.getMessageContext();  
		HttpServletRequest request = (HttpServletRequest)(mc.get(MessageContext.SERVLET_REQUEST));  
		String clientIp = request.getRemoteAddr();  
		
		boolean isXml = false;
		JSONObject jsonObj = null;
		
		if (JSONUtil.isJSON(body)) {
			jsonObj = JSONObject.fromObject(body);
		}else if (JSONUtil.isXML(body)) {
			Map requestMap = null;
			try {
				requestMap = XmlTool.documentToMap(body);
				isXml = true;
			} catch (DocumentException e) {
				e.printStackTrace();
				JSONObject result = JSONUtil.getErrMsg("传入的xml格式有错,正确格式:<request><serviceId>...</serviceId><action>...</action>...</request>" + e.getMessage());
				return result.toString();
			}

			try {
				jsonObj = XmlTool.xml2Json(body);
			} catch (DocumentException e) {
				JSONObject result = JSONUtil.getErrMsg("格式转换异常" + e.getMessage());
				return result.toString();
			}
		} else {
			JSONObject result = JSONUtil.getErrMsg("调用格式错误！");
			JSONObject data = new JSONObject();
			String helpInfo = "服务调用指导：\n";
			helpInfo += "1、同时支持json和xml请求  "
					+ " 2、json请求格式：{serviceId:'',action:'',...},响应格式:{code:'',msg:'',body:{...}} "
					+ " 3、xml请求格式:<request><serviceId>...</serviceId><action>...</action>...</request>, "
					+ " 响应格式：<response><code>...</code><msg>...</msg><body>...</body></response>";
			data.put("helpInfo", helpInfo);
			result.put("data", data);
			return result.toString();
		}
		
		JSONObject result = httpService.service(jsonObj, clientIp);
		
		String retStr = result.toString();
		
		if (isXml) {
			try {
				JSONObject result1 = new JSONObject();
				result1.put("response", result);
				retStr = XmlTool.jsonToXml(result1.toString());
			} catch (Exception e) {
				log.info("JSON转XML失败！！！" + e.getMessage());
			}
		}

		return retStr;
	}
	
}
