package com.wysoft.https_base.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wysoft.https_base.kafka.MessageSender;
import com.wysoft.https_base.service.HttpService;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.XmlTool;

import net.sf.json.JSONObject;

@Controller
@CrossOrigin
public class HttpsController {
	@Autowired
	private Logger log;
	
	@Autowired
	private HttpService httpService;
	
	@Autowired
	private MessageSender sender;

	@RequestMapping("/httpServiceEntry")
	@ResponseBody
	public String HttpServiceEntry(String body, HttpServletRequest req, HttpServletResponse resp) {
		String reqId = "[" + UUID.randomUUID().toString() + "]";
		String clientIp = getIpAddr(req);
		
		StopWatch sw = new StopWatch();
		sw.start();
		
		if (StringUtils.isEmpty(body)) {

			JSONObject jsonObject = new JSONObject();
			Map<String, String[]> paraMap = req.getParameterMap();
			Set<String> paraSet = paraMap.keySet();
			Iterator<String> paraInter = paraSet.iterator();
			while (paraInter.hasNext()) {
				String keyVal = paraInter.next();
				jsonObject.element(keyVal, req.getParameter(keyVal));
			}
			body = jsonObject.toString();
			if ("{}".equals(body)) {
				// 获取请求参数
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader((ServletInputStream) req.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					body = sb.toString();
					br.close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		log.info(reqId + "请求：" + body);
		
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
				log.info(reqId + "响应：" + result);
				return result.toString();
			}

			try {
				jsonObj = XmlTool.xml2Json(body);
			} catch (DocumentException e) {
				JSONObject result = JSONUtil.getErrMsg("格式转换异常" + e.getMessage());
				log.info(reqId + "响应：" + result);
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
			log.info(reqId + "响应：" + result);
			return result.toString();
		}

		JSONObject result = httpService.service(jsonObj, clientIp);
		
		sw.stop();
		long time = sw.getTotalTimeMillis();
		log.info(String.format("服务耗时：%d ms.",time));
		result.put("time",String.format("%d ms",time));
		
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
		
		log.info(reqId + "响应：" + retStr);
		
		return retStr;
	}
	
	/**
	 * 集成Kafka中间件处理异步消息.
	 * 说明：适用于非实时响应业务处理，不支持即时响应.
	 * 
	 * @param body
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping("/messageServiceEntry")
	@ResponseBody
	public String messageServiceEntry(String body, HttpServletRequest req, HttpServletResponse resp) {
		String reqId = "[" + UUID.randomUUID().toString() + "]";
		JSONObject result = JSONUtil.getResult();
		
		StopWatch sw = new StopWatch();
		sw.start();
		
		/**
		 * 请求解析
		 */
		if (StringUtils.isEmpty(body)) {
			JSONObject jsonObject = new JSONObject();
			Map<String, String[]> paraMap = req.getParameterMap();
			Set<String> paraSet = paraMap.keySet();
			Iterator<String> paraInter = paraSet.iterator();
			while (paraInter.hasNext()) {
				String keyVal = paraInter.next();
				jsonObject.element(keyVal, req.getParameter(keyVal));
			}
			body = jsonObject.toString();
			if ("{}".equals(body)) {
				// 获取请求参数
				BufferedReader br;
				try {
					br = new BufferedReader(new InputStreamReader((ServletInputStream) req.getInputStream(), "utf-8"));
					StringBuffer sb = new StringBuffer("");
					String temp;
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					body = sb.toString();
					br.close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		log.info("RECV MSG: " + reqId + body);
		
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
				result = JSONUtil.getErrMsg("传入的消息格式有误,正确格式:<request><topic>...</topic><message>...</message>...</request>" + e.getMessage());
				return result.toString();
			}

			try {
				jsonObj = XmlTool.xml2Json(body);
			} catch (DocumentException e) {
				result = JSONUtil.getErrMsg("格式转换异常" + e.getMessage());
				return result.toString();
			}
		} else {
			result = JSONUtil.getErrMsg("调用格式错误！");
			JSONObject data = new JSONObject();
			String helpInfo = "服务调用指导：\n";
			helpInfo += "1、同时支持json和xml请求  "
					+ " 2、json请求格式：{topic:'',message:{...}},响应格式:{code:'',msg:'',data:{...}} "
					+ " 3、xml请求格式:<request><topic>...</topic><message>...</message>...</request>, "
					+ " 响应格式：<response><code>...</code><msg>...</msg><data>...</data></response>";
			data.put("helpInfo", helpInfo);
			result.put("data", data);
			return result.toString();
		}
		
		/**
		 * 发送消息
		 */
		
		String topic = JSONUtil.getString(jsonObj,"topic");
		JSONObject message = jsonObj.getJSONObject("message");
		
		if(StringUtils.isEmpty(topic) || message == null) {
			return JSONUtil.getErrMsg("消息格式不正确！").toString();
		}
		
		ListenableFuture future = sender.send(topic, message);
		
		try {
			future.get();
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}
				
		if(future.isDone()) {
			/**
			 * 响应处理
			 */
			sw.stop();
			long time = sw.getTotalTimeMillis();
			log.info(String.format("服务耗时：%d ms.",time));
			result.put("time",String.format("%d ms",time));
			
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
			
			log.info("REPLY: " + reqId + retStr);
			
			return retStr;
		}else {
			return JSONUtil.getErrMsg("消息发送失败!").toString();
		}
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
}
