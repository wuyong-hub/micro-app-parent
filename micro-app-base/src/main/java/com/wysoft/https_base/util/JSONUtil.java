package com.wysoft.https_base.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class JSONUtil {
	public static final int SERVICE_OK = 200;
	public static final int SERVICE_NOTFOUND = 404;
	public static final int SERVICE_EXCEPTION = 500;

	public static String getString(JSONObject json,String key){
		String value = "";
		if(json.has(key)) {
			value = json.getString(key);
			value = value == null || "null".equals(value) ? "" : value;
		}
		return value;
	}
	
	public static int getInt(JSONObject json,String key){
		int value = 0;
		if(json.has(key)) {
			value = json.getInt(key);
		}
		return value;
	}
	
	public static JSONObject getResult(String ...messages) {
		JSONObject result = new JSONObject();
		result.put("code", SERVICE_OK);
		if(messages != null && messages.length > 0) {
			result.put("msg", messages[0]);
		}else {
			result.put("msg", "调用成功！");
		}
		return result;
	}
	
	public static  JSONObject getErrMsg(String errorMsg) {
		JSONObject json = new JSONObject();
		json.put("code", SERVICE_EXCEPTION);
		json.put("msg", errorMsg);
		return json;
	}
	
	public static  JSONObject getNotFoundMsg(String errorMsg) {
		JSONObject json = new JSONObject();
		json.put("code", SERVICE_NOTFOUND);
		json.put("msg", errorMsg);
		return json;
	}
	
	/**
	 * 判断是否是json 字符串.
	 * 
	 * @param jsonStr 目标串
	 * @return boolean
	 */
	public static  boolean isJSON(String jsonStr) {
		try {
			JSONObject.fromObject(jsonStr);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断是否是xml结构.
	 * @param value 目标串
	 * @return boolean
	 */
	public static  boolean isXML(String value) {
		try {
			DocumentHelper.parseText(value);
		} catch (DocumentException e) {
			return false;
		}
		return true;

	}
	
	/**
	 * 根据请求参数初始化分页对象.
	 * 参数说明：
	 * pageNumber : 当前页
	 * pageSize : 每页条数
	 * sort : 排序字段
	 * order : 排序方向
	 * @param json JSON参数
	 * @return Pageable
	 */
	public static Pageable createPage(JSONObject json) {
		Pageable pageable = null;
		// 获取分页信息
		int pageNumber = JSONUtil.getInt(json, "pageNumber"), 
				pageSize = JSONUtil.getInt(json, "pageSize");

		String sort = JSONUtil.getString(json, "sort"), 
				order = JSONUtil.getString(json, "order");

		if (pageNumber < 1) {
			pageNumber = 1;
		}

		if (pageSize < 1) {
			pageSize = 10;
		}

		if (StringUtils.isNotBlank(sort)) {
			Sort.Direction d = null;
			if (StringUtils.isBlank(order)) {
				d = Sort.Direction.ASC;
			} else {
				d = Sort.Direction.valueOf(order.toUpperCase());
			}
			Sort s = new Sort(d, sort);
			pageable = PageRequest.of(pageNumber - 1, pageSize, s);
		} else {
			pageable = PageRequest.of(pageNumber - 1, pageSize);
		}

		return pageable;
	}
}
