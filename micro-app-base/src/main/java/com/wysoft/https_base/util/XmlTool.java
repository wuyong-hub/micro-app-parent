package com.wysoft.https_base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * xml工具类
 * 
 * @author sleep 2016-09-13
 */
public class XmlTool {
	/**
	 * String 转 org.dom4j.Document.
	 * 
	 * @param xml XML
	 * @return {@link Document}
	 * @throws DocumentException 文档异常
	 */
	public static Document strToDocument(String xml) throws DocumentException {
		return DocumentHelper.parseText(xml);
	}

	/**
	 * org.dom4j.Document 转 com.alibaba.fastjson.JSONObject.
	 * 
	 * @param xml XML
	 * @return {@link JSONObject}
	 * @throws DocumentException 文档异常
	 */
	public static JSONObject documentToJSONObject(String xml)
			throws DocumentException {
		return elementToJSONObject(strToDocument(xml).getRootElement());
	}

	/**
	 * org.dom4j.Element to com.alibaba.fastjson.JSONObject.
	 * 
	 * @param node 节点 
	 * @return {@link JSONObject}
	 */
	public static JSONObject elementToJSONObject(Element node) {
		JSONObject result = new JSONObject();
		
		List<Attribute> listAttr = node.attributes();
		for (Attribute attr : listAttr) {
			result.put(attr.getName(), attr.getValue());
		}

		List<Element> listElement = node.elements();
		if (!listElement.isEmpty()) {
			for (Element e : listElement) {
				if (e.attributes().isEmpty() && e.elements().isEmpty()) 
					result.put(e.getName(), e.getTextTrim());
				else {
					if (!result.containsKey(e.getName())) 
						result.put(e.getName(), new JSONArray());
					((JSONArray) result.get(e.getName()))
							.add(elementToJSONObject(e));
				}
			}
		}
		return result;
	}

	/**
	 * xml转Map.
	 * @param xml XML
	 * @return {@link Map}
	 * @throws DocumentException 文档异常
	 */
	public static Map documentToMap(String xml) throws DocumentException {
		return elementToMap(strToDocument(xml).getRootElement());
	}

	/**
	 * 元素节点转Map.
	 * @param node 节点
	 * @return {@link Map}
	 */
	public static Map elementToMap(Element node) {
		Map obj = new HashMap();
		List<Attribute> listAttr = node.attributes();
		for (Attribute attr : listAttr) {
			obj.put(attr.getName(), attr.getValue());
		}

		List<Element> listElement = node.elements();
		if (!listElement.isEmpty()) {
			for (Element e : listElement) {

				List<Element> eList = node.elements(e.getName());
				if (eList != null && eList.size() > 1) {
					if (obj.get(e.getName()) == null) {
						obj.put(e.getName(), elementTolist(eList));
					}

				} else if (!e.getTextTrim().equals("")) {
					obj.put(e.getName(), e.getTextTrim());
				} else {
					Map mape = elementToMap(e);
					if (mape == null || mape.size() == 0) {
						obj.put(e.getName(), "");
					} else {
						obj.put(e.getName(), mape);
					}

				}

			}
		} else if (!node.getTextTrim().equals("")) {
			obj.put(node.getName(), node.getTextTrim());
		}
		return obj;
	}
	
	
	/**
     * Json to xml string.
     *
     * @param json the json
     * @return the string
     */
    public static String jsonToXml(String json){
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            com.alibaba.fastjson.JSONObject jObj = JSON.parseObject(json);
           
            jsonToXmlstr(jObj,buffer);
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
 
 
    /**
     * Json to xmlstr string.
     *
     * @param jObj   the j obj
     * @param buffer the buffer
     * @return the string
     */
    public static String jsonToXmlstr(com.alibaba.fastjson.JSONObject jObj,StringBuffer buffer ){
        Set<Map.Entry<String, Object>> se = jObj.entrySet();
        for(Iterator<Map.Entry<String, Object>> it = se.iterator(); it.hasNext(); )
        {
            Map.Entry<String, Object> en = it.next();
            if(en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONObject")){
                buffer.append("<"+en.getKey()+">");
                com.alibaba.fastjson.JSONObject jo = jObj.getJSONObject(en.getKey());
                jsonToXmlstr(jo,buffer);
                buffer.append("</"+en.getKey()+">");
            }else if(en.getValue().getClass().getName().equals("com.alibaba.fastjson.JSONArray")){
                com.alibaba.fastjson.JSONArray jarray = jObj.getJSONArray(en.getKey());
                for (int i = 0; i < jarray.size(); i++) {
                    buffer.append("<"+en.getKey()+">");
                    com.alibaba.fastjson.JSONObject jsonobject =  jarray.getJSONObject(i);
                    jsonToXmlstr(jsonobject,buffer);
                    buffer.append("</"+en.getKey()+">");
                }
            }else{
                buffer.append("<"+en.getKey()+">"+en.getValue());
                buffer.append("</"+en.getKey()+">");
            }
        }
        return buffer.toString();
    }
    
    
	/**
	 * 元素列表转List.
	 * @param eList 源数据
	 * @return List
	 */
	public static List elementTolist(List<Element> eList) {
		List list = new ArrayList();

		for (Element e : eList) {
			list.add(elementToMap(e));

		}
		return list;
	}

	public static void main(String[] args) {
		String xml = "<MapSet>" + "<MapGroup id='Sheboygan'>" + "<Map>"
				+ "<Type>MapGuideddddddd</Type>"
				+ "<SingleTile>true</SingleTile>" + "<Extension>"
				+ "<ResourceId>ddd</ResourceId>" + "</Extension>" + "</Map>"
				+ "<Map>" + "<Type>ccc</Type>" + "<SingleTile>ggg</SingleTile>"
				+ "<Extension>" + "<ResourceId>aaa</ResourceId>"
				+ "</Extension>" + "</Map>" + "<Extension />" + "</MapGroup>"
				+ "<ddd>" + "33333333" + "</ddd>" + "<ddd>" + "444" + "</ddd>"
				+ "</MapSet>";

		System.out.println(xml);

		try {
			//System.out.println(xml2Json(xml).toString());
			System.out.println(jsonToXml(xml2Json(xml).toString()));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	  /**
     * xml转json
     * @param xmlStr xmlStr
     * @return JSONObject
     * @throws DocumentException 文档异常
     */
    public static JSONObject xml2Json(String xmlStr) throws DocumentException{
        Document doc= DocumentHelper.parseText(xmlStr);
        JSONObject json=new JSONObject();
        dom4j2Json(doc.getRootElement(), json);
        return json;
    }

    /**
     * xml转json.
     * @param element 节点
     * @param json 目标载体
     */
    public static void dom4j2Json(Element element,JSONObject json){
        //如果是属性
        for(Object o:element.attributes()){
            Attribute attr=(Attribute)o;
            if(StringUtils.isNotEmpty(attr.getValue())){
                //json.put("@"+attr.getName(), attr.getValue());
            	json.put(attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl=element.elements();
        if(chdEl.isEmpty()&&StringUtils.isNotEmpty(element.getText())){//如果没有子元素,只有一个值
            json.put(element.getName(), element.getText());
        }

        for(Element e:chdEl){//有子元素
            if(!e.elements().isEmpty()){//子元素也有子元素
                JSONObject chdjson=new JSONObject();
                dom4j2Json(e,chdjson);
                Object o=json.get(e.getName());
                if(o!=null){
                    JSONArray jsona=null;
                    if(o instanceof JSONObject){//如果此元素已存在,则转为jsonArray
                        JSONObject jsono=(JSONObject)o;
                        json.remove(e.getName());
                        jsona=new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if(o instanceof JSONArray){
                        jsona=(JSONArray)o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                }else{
                    if(!chdjson.isEmpty()){
                        json.put(e.getName(), chdjson);
                    }
                }


            }else{//子元素没有子元素
                for(Object o:element.attributes()){
                    Attribute attr=(Attribute)o;
                    if(StringUtils.isNotEmpty(attr.getValue())){
                        json.put("@"+attr.getName(), attr.getValue());
                    }
                }
                if(!e.getText().isEmpty()){
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

}