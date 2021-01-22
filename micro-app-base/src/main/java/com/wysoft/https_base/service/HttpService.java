package com.wysoft.https_base.service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.zookeeper.KeeperException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.action.ServiceAccessPoint;
import com.wysoft.https_base.configuration.AppConfig;
import com.wysoft.https_base.util.HttpUtils;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_base.util.SpringUtils;
import com.wysoft.https_base.util.XmlTool;
import com.wysoft.https_base.zookeeper.ZkApi;

import net.sf.json.JSONObject;

@Service
public class HttpService {
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private AuthVerifyService authVerifyService;
	@Autowired
	private Logger log;
	@Autowired
	private ZkApi zkApi;

	public JSONObject service(JSONObject jsonObj, String clientIp) {
		JSONObject result = null;
		String serviceId = JSONUtil.getString(jsonObj, "serviceId");
		String action = JSONUtil.getString(jsonObj, "action");

		if (StringUtils.isEmpty(serviceId)) {
			result = JSONUtil.getErrMsg("服务名【serviceId】不能为空！");
			return result;
		}
		
		// 授权验证
		if (appConfig.isNeedAuth()) {
			String authCode = JSONUtil.getString(jsonObj, "authCode");
			JSONObject authResult = authVerifyService.verify(authCode, clientIp);
			if (authResult.getInt("code") < 0) {
				result = JSONUtil.getErrMsg(authResult.getString("msg"));
				return result;
			}
		}

		// 服务调用
		ServiceAccessPoint ap = SpringUtils.getBean(serviceId);

		if (ap == null) {
			result = remoteService(serviceId, action, jsonObj);
			return result;
		} else {
			if (StringUtils.isEmpty(action)) {
				result = ap.doAction(jsonObj);
			} else {
				Method[] methods = ap.getClass().getDeclaredMethods();
				Method serviceMethod = null;// 调用最终service
				for (Method m : methods) {
					if (action.equals(m.getName())) {
						serviceMethod = m;
						break;
					}
				}
				if (serviceMethod != null) {
					try {
						result = (JSONObject)serviceMethod.invoke(ap, jsonObj);
					} catch (Exception e) {
						result = JSONUtil.getErrMsg("调用发生异常！[" + e.getMessage() + "]");
						e.printStackTrace();
						return result;
					}
				} else {
					result = JSONUtil.getErrMsg("方法名【" + action + "】不存在！");
					return result;
				}
			}
		}

		return result;
	}

	private JSONObject remoteService(String serviceId, String action, JSONObject jsonObj) {
		JSONObject result = null;
		if (StringUtils.isEmpty(action)) {
			action = "doAction";
		}

		String path = appConfig.getZookeeperRoot() + "/" + serviceId + "/" + action;

		try {
			if (!zkApi.exists(path, false)) {
				result = JSONUtil.getErrMsg("服务【" + path + "】未实现！");
				return result;
			}
			List<String> children = zkApi.getChildren(path);
			if (children == null || children.size() < 1) {
				result = JSONUtil.getErrMsg("服务【" + path + "】不在线！");
				return result;
			}
			int index = 0;
			if (children.size() > 1) {
				index = loadBalance(children.size());
				log.info("节点" + index + "执行负载！");
			}
			
			String child = children.get(index);
			path += "/" + child;
			String remoteUrl = zkApi.getData(path, null);
			return JSONObject.fromObject(HttpUtils.httpPost(remoteUrl, jsonObj.toString()));
			
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private int loadBalance(int size) {
		Random rand = new Random();
		return rand.nextInt(size);
	}
}
