package com.wysoft.https_auth.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.wysoft.https_auth.action.UserService;
import com.wysoft.https_auth.model.UaamAuthLogin;
import com.wysoft.https_auth.model.UaamUser;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.XmlTool;

import net.sf.json.JSONObject;

@Controller
@CrossOrigin
public class PageController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private Logger log;
	
	@RequestMapping("/login")
	public ModelAndView login(HttpServletRequest req){
		String redirectPath = req.getParameter("redirectPath");
		if(StringUtils.isEmpty(redirectPath)) {
			redirectPath = "/portal";
		}
		ModelAndView mv = new ModelAndView();
		mv.addObject("redirectPath", redirectPath);
		mv.setViewName("login");
		return mv;
	}
	
	@RequestMapping("/logout")
	public ModelAndView logout(HttpServletRequest req){
		req.getSession().invalidate();
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}
	
	@RequestMapping("/doLogin")
	@ResponseBody
	public JSONObject doLogin(String username,String password,HttpServletRequest req){
		JSONObject json = new JSONObject();
		json.put("username", username);
		json.put("password", password);
		JSONObject jsonResult = userService.doAction(json);
		if(jsonResult.getInt("code") == JSONUtil.SERVICE_OK){
			JSONObject userJson = jsonResult.getJSONObject("data");
			UaamUser user = (UaamUser) JSONObject.toBean(userJson, UaamUser.class);
			req.getSession().setAttribute("session_user", user);
		}
		return jsonResult;
	}
	
	@RequestMapping("/httpAuth")
	@ResponseBody
	public String httpAuth(String body, HttpServletRequest req, HttpServletResponse resp) {
		String reqId = "[" + UUID.randomUUID().toString() + "]";
		
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
				JSONObject result = JSONUtil.getErrMsg("传入的xml格式有错,正确格式:<request><username>...</username><password>...</password>...</request>" + e.getMessage());
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
					+ " 2、json请求格式：{username:'',password:'',...},响应格式:{code:'',msg:'',data:{...}} "
					+ " 3、xml请求格式:<request><username>...</username><password>...</password>...</request>, "
					+ " 响应格式：<response><code>...</code><msg>...</msg><data>...</data></response>";
			data.put("helpInfo", helpInfo);
			result.put("data", data);
			log.info(reqId + "响应：" + result);
			return result.toString();
		}

		JSONObject result = userService.shiroLogin(jsonObj);
		
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

	
	@RequestMapping("/portal")
	public ModelAndView portal(HttpServletRequest req){
		ModelAndView mv = new ModelAndView();
		mv.addObject("serviceUrl", "https://localhost:8443/httpServiceEntry");
		String testReq = "{\n"
			    + "    'serviceId':'test',\n"
			    + "    'authCode':'5Yib5Lia5oWn5bq3fGN5X3VzZXIxfGFkbWluQDEyMw=='\n"
			+ "}";
		mv.addObject("reqData", testReq);
		mv.setViewName("portal");
		return mv;
	}
	
	@RequestMapping("/ehrview")
	public ModelAndView ehrview(HttpServletRequest req){
		ModelAndView mv = new ModelAndView();
		mv.setViewName("ehrview");
		return mv;
	}
	
	@RequestMapping("/qrCode")
	public void qrCode(HttpServletResponse res,String url,String uuid,String appid) throws Exception{
        res.setHeader("Cache-Control", "no-store");
        res.setHeader("Pragma", "no-cache");
        res.setDateHeader("Expires", 0);
        res.setContentType("image/png");

        url +="?k=" + uuid + "&appid=" + appid;
        
        UaamAuthLogin authLogin = new UaamAuthLogin();
        
        authLogin.setAuthuuid(uuid);
        authLogin.setAppid(appid);
        authLogin.setAuthtime(new Date());
        authLogin.setLoginstatus(0);
        
        userService.saveAuthLogin(authLogin);
 
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
 
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 200, 200, hints);
 
        MatrixToImageWriter.writeToStream(bitMatrix, "png", res.getOutputStream());
	}
	
	/**
	 * 二维码/短信认证登录.
	 * @param uuid
	 * @return
	 */
	@RequestMapping("/authLogin")
	@ResponseBody
	public JSONObject authLogin(String uuid) {
		JSONObject jsonResult = userService.getAuthLogin(uuid);
		
		return jsonResult;
	}
}
