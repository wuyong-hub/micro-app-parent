package com.wysoft.https_lottery.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.action.BaseService;
import com.wysoft.https_base.annotation.RemoteMethod;
import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_lottery.model.SsqNum;
import com.wysoft.https_lottery.model.SsqRecord;
import com.wysoft.https_lottery.service.SsqCalcServcie;
import com.wysoft.https_lottery.service.SsqDataService;
import com.wysoft.https_lottery.vo.KeyValue;

import net.sf.json.JSONObject;

/**
 * 彩票信息服务.
 * @author Wuyong
 *
 */
@Service("lotteryService")
public class LotteryService extends BaseService{
	
	@Autowired
	private SsqDataService dataService;
	
	@Autowired
	private SsqCalcServcie calcService;

	/**
	 * 同步,计算数据.
	 * @param json 不需要输入
	 * @return {syncCount : "同步数据条数",nQh : "累计期号",mQh : "遗失期号"}
	 */
	@RemoteMethod
	public JSONObject syncData(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		
		//同步数据
		int syncCount = dataService.syncData();
		
		//累计号码出现次数
		String qh1 = calcService.calcCountOfNum();
		
		//累计号码遗失次数
		String qh2 =  calcService.calcMiss();
		
		JSONObject data = new JSONObject();
		data.put("syncCount",syncCount);
		data.put("nQh",qh1);
		data.put("mQh",qh2);
		result.put("data", data);
		return result;
	}
	
	/**
	 * 查询记录.
	 * @param json {pageNumber,pageSize,sort,order}
	 * @return {@link SsqRecord}
	 */
	@RemoteMethod
	public JSONObject findRecords(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		Pageable pageable = JSONUtil.createPage(json);
		
		Page<SsqRecord> page = calcService.findRecords(pageable);
		
		result.put("data",page.getContent());
		result.put("totalPages",page.getTotalPages());
		result.put("pageNumber",pageable.getPageNumber());
		result.put("pageSize",pageable.getPageSize());
		result.put("size",page.getSize());

		return result;
	}
	
	/**
	 * 近期热号追踪.
	 * @param json {qs,topSize}
	 * @return List<Map<String,Integer>>
	 */
	@RemoteMethod
	public JSONObject hotCodes(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		int qs = JSONUtil.getInt(json, "qs");
		int topSize = JSONUtil.getInt(json, "topSize");
		List<Map<Integer,Integer>> list = calcService.recentHotCode(qs, topSize);
		
		List<Map<String,Integer>> results = new ArrayList<>();
		
		for(Map<Integer,Integer> map : list) {
			for(Integer key : map.keySet()) {
				Map<String,Integer> item = new HashMap<>();
				item.put("number", key);
				item.put("times", map.get(key));
				results.add(item);
			}
		}
		result.put("data",results);
		return result;
	}
	
	/**
	 * 冷号追踪.
	 * @param json {topSize}
	 * @return List<Map<String,Integer>>
	 */
	@RemoteMethod
	public JSONObject coolCodes(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		int topSize = JSONUtil.getInt(json, "topSize");
		Map coolMap = calcService.coolCode(topSize);
		
		List<Map<String,Integer>> results = new ArrayList<>();
		
		for(Map<Integer,Integer> map : (List<Map<Integer,Integer>>)coolMap.get("list")) {
			for(Integer key : map.keySet()) {
				Map<String,Integer> item = new HashMap<>();
				item.put("number", key);
				item.put("times", map.get(key));
				results.add(item);
			}
		}
		
		coolMap.put("list", results);
		result.put("data",coolMap);
		return result;
	}
	
	/**
	 * 数字累计.
	 * @param json {topSize}
	 * @return {topData,lastData}
	 */
	@RemoteMethod
	public JSONObject totalTimesOfNum(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		int topSize = JSONUtil.getInt(json, "topSize");
		
		Map numMap = calcService.totalTimesOfNum(topSize);
		
		List<SsqNum> topData = (List<SsqNum>) numMap.get("topData");
		
		List<SsqNum> lastData = (List<SsqNum>) numMap.get("lastData");
		
		List<Map<String,Integer>> topList = new ArrayList<>();
		
		List<Map<String,Integer>> lastList = new ArrayList<>();
		
		for(SsqNum num : topData) {
			Map<String,Integer> item = new HashMap<>();
			item.put("number", num.getN());
			item.put("times", num.getC());
			topList.add(item);
		}
		
		for(SsqNum num : lastData) {
			Map<String,Integer> item = new HashMap<>();
			item.put("number", num.getN());
			item.put("times", num.getC());
			lastList.add(item);
		}
		
		numMap.put("topData", topList);
		numMap.put("lastData", lastList);
		
		result.put("data", numMap);
		
		return result;
	}
	
	/**
	 * 推荐号码组合.
	 * @param json {qs,topSize}
	 * @return {integer : integer}
	 */
	@RemoteMethod
	public JSONObject recommendCodes(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		int qs = JSONUtil.getInt(json, "qs");
		int topSize = JSONUtil.getInt(json, "topSize");
		Map<String,Integer> data = calcService.recommendCodes(qs,topSize);
		result.put("data", data);
		return result;
	}
	
	/**
	 * 综合分析结果.
	 * @param json {qs,topSize}
	 * @return {title,codes}
	 */
	@RemoteMethod
	public JSONObject analyseData(JSONObject json) {
		JSONObject result = JSONUtil.getResult();
		int qs = JSONUtil.getInt(json, "qs");
		int topSize = JSONUtil.getInt(json, "topSize");
		
		//推荐号
		Map<String,Integer> recommentListMap = new HashMap<>();
		
		//热号
		List<Map<Integer,Integer>> hotList = calcService.recentHotCode(qs, topSize);
		
		List<Map<String,Object>> rList = new ArrayList<>();
		
		Map<String,Object> hotCodeMap = new HashMap<>();
		
		hotCodeMap.put("title", "近期热号");
		
		String hotCodeStr = "";
		
		for(Map<Integer,Integer> map : hotList) {
			for(Integer key : map.keySet()) {
				hotCodeStr += key + ",";
				if(!recommentListMap.containsKey(key + "")) {
					recommentListMap.put(key + "",1);
				}else {
					recommentListMap.put(key + "",recommentListMap.get(key+"") + 1);
				}
			}
		}
		
		hotCodeStr = hotCodeStr.substring(0,hotCodeStr.lastIndexOf(","));
		
		hotCodeMap.put("codes", hotCodeStr);
		
		rList.add(hotCodeMap);
		
		//冷号
		List<Map<Integer, Integer>> coollist = (List<Map<Integer, Integer>>) calcService.coolCode(topSize).get("list");
		
		Map<String,Object> coolCodeMap = new HashMap<>();
		
		coolCodeMap.put("title", "遗漏最多");
		
		String coolCodeStr = "";
		
		List<Map<String,Integer>> results = new ArrayList<>();
		
		for(Map<Integer,Integer> map : coollist) {
			for(Integer key : map.keySet()) {
				coolCodeStr += key + ",";
				if(!recommentListMap.containsKey(key + "")) {
					recommentListMap.put(key + "",1);
				}else {
					recommentListMap.put(key + "",recommentListMap.get(key+"") + 1);
				}
			}
		}
		
		coolCodeStr = coolCodeStr.substring(0,coolCodeStr.lastIndexOf(","));
		
		coolCodeMap.put("codes", coolCodeStr);
		
		rList.add(coolCodeMap);
		
		//累计出现
		Map numMap = calcService.totalTimesOfNum(topSize);
		
		List<SsqNum> topData = (List<SsqNum>) numMap.get("topData");
		
		List<SsqNum> lastData = (List<SsqNum>) numMap.get("lastData");
		
		Map<String,Object> topMap = new HashMap<>();
		
		topMap.put("title", "最多出现");
		
		String topStr = "";

		for(SsqNum num : topData) {
			topStr += num.getN() + ",";
			if(!recommentListMap.containsKey(num.getN() + "")) {
				recommentListMap.put(num.getN() + "",1);
			}else {
				recommentListMap.put(num.getN() + "",recommentListMap.get(num.getN() + "") + 1);
			}
		}
		
		topStr = topStr.substring(0,topStr.lastIndexOf(","));
		
		topMap.put("codes", topStr);
		
		rList.add(topMap);
		
		Map<String,Object> lastMap = new HashMap<>();
		
		lastMap.put("title", "最少出现");
		
		String lastStr = "";
		
		for(SsqNum num : lastData) {
			lastStr += num.getN() + ",";
			if(!recommentListMap.containsKey(num.getN() + "")) {
				recommentListMap.put(num.getN() + "",1);
			}else {
				recommentListMap.put(num.getN() + "",recommentListMap.get(num.getN() + "") + 1);
			}
		}
		
		lastStr = lastStr.substring(0,lastStr.lastIndexOf(","));
		
		lastMap.put("codes", lastStr);
		
		rList.add(lastMap);
		
		//推荐号
		Map<String,Object> recommentMap = new HashMap<>();
		
		recommentMap.put("title", "推荐号码");
		
		String recommendStr = "";
		List<KeyValue<String,Integer>> kvList = new ArrayList<>();
		
		for(String key : recommentListMap.keySet()) {
			KeyValue<String,Integer> kv = new KeyValue<>();
			kv.setKey(key);
			kv.setValue(recommentListMap.get(key));
			kvList.add(kv);
		}
		
		int max = 0,pos = 0;
		for(int i = 0; i < kvList.size(); i ++) {
			max = kvList.get(i).getValue();
			pos = i;
			for(int j = i + 1;j < kvList.size(); j ++) {
				if(kvList.get(j).getValue() > max) {
					max = kvList.get(j).getValue();
					pos = j;
				}
			}
			if(pos != i) {
				KeyValue<String,Integer> temp = kvList.get(i);
				kvList.set(i, kvList.get(pos));
				kvList.set(pos, temp);
			}
		}
		
		for(KeyValue<String,Integer> kv : kvList) {
			recommendStr += kv.getKey() + ",";
		}
		
		recommendStr = recommendStr.substring(0,recommendStr.lastIndexOf(","));
		
		recommentMap.put("codes", recommendStr);
		
		rList.add(recommentMap);

		result.put("data",rList);
		
		return result;
	}
}
