package com.wysoft.https_base.action;

import net.sf.json.JSONObject;

/**
 * 以JSON作为消息标准的接口定义.
 * @author Wuyong
 *
 */
public interface ServiceAccessPoint {
	 public JSONObject doAction(JSONObject json);
}
