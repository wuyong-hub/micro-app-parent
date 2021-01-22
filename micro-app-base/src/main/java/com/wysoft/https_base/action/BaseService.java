package com.wysoft.https_base.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;

import net.sf.json.JSONObject;

/**
 * 基础服务.
 * serviceId : base
 * @author Wuyong
 *
 */
@Service("base")
public class BaseService implements ServiceAccessPoint{

	/**
	 * 默认方法.
	 * @param json 不需要输入
	 * @return JSONObject
	 */
	@Override
	@RemoteMethod
	public JSONObject doAction(JSONObject json) {
		return JSONUtil.getResult();
	}
	
	/**
	 * 用法说明.
	 * @param json 不需要输入
	 * @return JSONObject
	 */
	@RemoteMethod
	public JSONObject help(JSONObject json){
		JSONObject result = JSONUtil.getResult();
		JSONObject data = new JSONObject();
		String helpInfo = "服务调用指导：\n";
		helpInfo += "1、同时支持json和xml请求  "
				+ " 2、json请求格式：{serviceId:'',action:'',...},响应格式:{code:'',msg:'',body:{...}} "
				+ " 3、xml请求格式:<request><serviceId>...</serviceId><action>...</action>...</request>, "
				+ " 响应格式：<response><code>...</code><msg>...</msg><body>...</body></response>";
		data.put("helpInfo", helpInfo);
		result.put("data", data);
		return result;
	}
	
	/**
	 * 测试方法.
	 * @param json 不需要输入
	 * @return date 当前服务器时间
	 */
	@RemoteMethod
	public JSONObject test(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		JSONObject data = new JSONObject();
		data.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		result.put("data", data);
		return result;
	}
}
