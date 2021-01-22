package com.wysoft.https_lottery.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.wysoft.https_base.util.JSONUtil;
import com.wysoft.https_lottery.algorithm.MinorTrendProcessor;
import com.wysoft.https_lottery.dao.BaseDao;
import com.wysoft.https_lottery.dao.GlobalConfigDao;
import com.wysoft.https_lottery.dao.SsqMissDao;
import com.wysoft.https_lottery.dao.SsqNumDao;
import com.wysoft.https_lottery.dao.SsqRecordDao;
import com.wysoft.https_lottery.model.GlobalConfig;
import com.wysoft.https_lottery.model.SsqMiss;
import com.wysoft.https_lottery.model.SsqNum;
import com.wysoft.https_lottery.model.SsqRecord;

import net.sf.json.JSONObject;

@Service("ssqCalcService") 
@Transactional
public class SsqCalcServcie {
	@Autowired
	private GlobalConfigDao configDao;
	
	@Autowired
	private SsqRecordDao ssqRecordDao;
	
	@Autowired
	private SsqNumDao ssqNumDao;
	
	@Autowired
	private SsqMissDao ssqMissDao;
	
	@Autowired
	private BaseDao baseDao;
	
	/**
	 * 号码历史出现次数统计.
	 * @return
	 */
	public String calcCountOfNum() {
		List<GlobalConfig> cfgs = configDao.findByConfigCode("SSQ_NUM_CALC_STATUS");
		if(cfgs == null || cfgs.size() <= 0){
			return null;
		}
		GlobalConfig statusCfg = cfgs.get(0);
		
		final String qh = statusCfg.getConfigValue();
		
		List<SsqRecord> records = null;
		
		if(StringUtils.isEmpty(qh)){
			for(Integer n = 1; n <= 33; n ++){
				SsqNum num = new SsqNum();
				num.setN(n);
				num.setC(0);
				ssqNumDao.save(num);
			}
			records = ssqRecordDao.findAll(Sort.by(Direction.ASC, "qh"));
		}else{
			records = ssqRecordDao.findAll(new Specification<SsqRecord>() {

				@Override
				public Predicate toPredicate(Root<SsqRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
					return cb.greaterThan(root.<String> get("qh"), qh);
				}
				
			},Sort.by(Direction.ASC, "qh"));
		}
		
		if(records == null || records.size() <= 0){
			return qh;
		}
		
		String newQh = qh;
		
		List<SsqNum> ns = ssqNumDao.findAll();
		
		Map<Integer,Integer> nMap = new HashMap<Integer,Integer>();
		
		for(Object on : ns){
			SsqNum n = (SsqNum)on;
			nMap.put(n.getN(),n.getC());
		}
		
		for(Object record : records){
			SsqRecord r = (SsqRecord)record;
			
			nMap.put(r.getR1(), nMap.get(r.getR1()) + 1);
			nMap.put(r.getR2(), nMap.get(r.getR2()) + 1);
			nMap.put(r.getR3(), nMap.get(r.getR3()) + 1);
			nMap.put(r.getR4(), nMap.get(r.getR4()) + 1);
			nMap.put(r.getR5(), nMap.get(r.getR5()) + 1);
			nMap.put(r.getR6(), nMap.get(r.getR6()) + 1);

			newQh = r.getQh();
		}
		
		for(Map.Entry<Integer, Integer> e : nMap.entrySet()){
			SsqNum num = ssqNumDao.getOne(e.getKey());
			num.setC(e.getValue());
			ssqNumDao.save(num);
		}
		
		if(StringUtils.isNotEmpty(qh)){
			statusCfg.setConfigValue(qh);
			configDao.save(statusCfg);
		}
		
		return newQh;
	}

	/**
	 * 遗漏次数统计.
	 * @return
	 */
	public String calcMiss() {
		String curQh = ssqMissDao.uniqueResult();
		if(curQh == null){
			SsqMiss mi = new SsqMiss();
			mi.setQh("00000");
			JSONObject o = new JSONObject();
			for(Integer n = 1; n <= 33; n ++){
				o.put(n, 1000);
			}
			
			mi.setMissInfo(o.toString());
			ssqMissDao.save(mi);
			curQh = mi.getQh();
		}
		
		SsqMiss curMi = ssqMissDao.findById(curQh).get();
		
		final String qh = curQh;

		List<SsqRecord> records = ssqRecordDao.findAll(new Specification<SsqRecord>() {

			@Override
			public Predicate toPredicate(Root<SsqRecord> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				return cb.greaterThan(root.<String> get("qh"), qh);
			}
			
		}, Sort.by(Direction.ASC, "qh"));
		
		if(records == null || records.size() <= 0){
			return null;
		}
		
		for(Object record : records){
			SsqRecord r = (SsqRecord)record;
			SsqMiss nextMi = new SsqMiss();
			try {
				BeanUtils.copyProperties(nextMi, curMi);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			nextMi.setQh(r.getQh());
			JSONObject mi = JSONObject.fromObject(curMi.getMissInfo());
			for(Object key : mi.keySet()){
				Object value = mi.get(key);
				mi.put(key, (Integer)value + 1);
			}
			
			mi.put(r.getR1(), 0);
			mi.put(r.getR2(), 0);
			mi.put(r.getR3(), 0);
			mi.put(r.getR4(), 0);
			mi.put(r.getR5(), 0);
			mi.put(r.getR6(), 0);
			
			nextMi.setMissInfo(mi.toString());
			
			ssqMissDao.save(nextMi);
			
			curMi = nextMi;
		}
		
		return curMi.getQh();
	}

	
	private void put(Map<Integer,Integer> map,Integer num){
		if(map.containsKey(num)){
			map.put(num, map.get(num) + 1);
		}
		else{
			map.put(num, 1);
		}
	}
	
	/**
	 * 近期热号统计.
	 * @param qs 考查期数.
	 * @param topSize 热号个数.
	 * @return
	 */
	public List<Map<Integer,Integer>> recentHotCode(int qs,int topSize) {
		List  list = baseDao.find("select t from SsqRecord t order by t.qh desc", new Object[]{}, 1, qs);
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		
		for(Object s : list){
			SsqRecord r  = (SsqRecord) s;
			put(map,r.getR1());
			put(map,r.getR2());
			put(map,r.getR3());
			put(map,r.getR4());
			put(map,r.getR5());
			put(map,r.getR6());
		}
		
		List<Map<Integer,Integer>> results = new ArrayList<Map<Integer,Integer>>();
		
		List<Integer> keys = new ArrayList<Integer>();
		for(Integer k :map.keySet()){
			keys.add(k);
		}
		while(keys != null && keys.size() > 0){
			Integer max = -1,mk = -1,pos = -1;
			for(int i = 0; i < keys.size();i ++){
				Integer key = keys.get(i);
				if(max < map.get(key)){
					max = map.get(key);
					mk = key;
					pos = i;
				}
			}
			
			Map<Integer,Integer> m = new HashMap<Integer,Integer>();
			m.put(mk, map.get(mk));
			results.add(m);
			
			keys.remove(pos.intValue());
		}
		
		return results.subList(0, topSize);
	}
	
	
	/**
	 * 冷号追踪.
	 * @param topSize 取号个数
	 * @return Map
	 */
	public Map coolCode(int topSize) {
		List  list = baseDao.find("select t from SsqMiss t order by t.qh desc", new Object[]{}, 1, 1);
		
		if(list == null || list.size() <= 0) {
			return null;
		}
		
		SsqMiss sm = (SsqMiss) list.get(0);
		
		String qh = sm.getQh();
		
		String mi = sm.getMissInfo();
		
		JSONObject miJson = JSONObject.fromObject(mi);
		
		List<Map<Integer,Integer>> results = new ArrayList<>();
		
		List<String> keys = new ArrayList<>();
		for(Object k : miJson.keySet()){
			keys.add(k.toString());
		}
		while(keys != null && keys.size() > 0){
			Integer max = -1,mk = -1,pos = -1;
			for(int i = 0; i < keys.size();i ++){
				String key = keys.get(i);
				if(max < JSONUtil.getInt(miJson, key)){
					max = JSONUtil.getInt(miJson, key);
					mk = Integer.parseInt(key.toString());
					pos = i;
				}
			}
			
			Map<Integer,Integer> m = new HashMap<Integer,Integer>();
			m.put(mk, JSONUtil.getInt(miJson, mk + ""));
			results.add(m);
			
			keys.remove(pos.intValue());
		}
		
		Map map = new HashMap();
		
		map.put("qh", qh);
		map.put("list", results.subList(0, topSize));
		
		return map;
	}
	
	/**
	 * 累计出现次数统计.
	 * @param topSize 分别取出次数最多，最少条数
	 * @return Map
	 */
	public Map totalTimesOfNum(int topSize) {
		List  list = baseDao.find("select t from SsqNum t order by t.c desc");
		
		Map result = new HashMap();
		
		result.put("topData", list.subList(0, topSize));
		result.put("lastData", list.subList(list.size() - topSize, list.size()));
		
		return result;
	}
	
	/**
	 * 推荐组合.
	 * @return Map<Integer,Integer>
	 */
	public Map<String,Integer> recommendCodes(int qs,int topSize) {
		
		List<Map<Integer,Integer>> hotCodes = this.recentHotCode(qs, topSize);
		List<Map<Integer,Integer>> coolCodes = (List<Map<Integer, Integer>>) this.coolCode(topSize).get("list");
		Map numMap = this.totalTimesOfNum(topSize);
		List<SsqNum> topList = (List<SsqNum>) numMap.get("topData");
		List<SsqNum> lastList = (List<SsqNum>) numMap.get("lastData");
		
		Map<String,Integer> result = new HashMap<>();
		
		for(Map<Integer,Integer> map : hotCodes) {
			for(Integer n : map.keySet()) {
				result.put(n + "", 0);
			}
		}
		
		for(Map<Integer,Integer> map : coolCodes) {
			for(Integer n : map.keySet()) {
				String key = n + "";
				if(result.containsKey(key)) {
					result.put(key, result.get(key) + 1);
				}else {
					result.put(key, 0);
				}
			}
		}
		
		for(SsqNum num : topList) {
			String key = num.getN() + "";
			if(result.containsKey(key)) {
				result.put(key, result.get(key) + 1);
			}else {
				result.put(key, 0);
			}
		}
		
		for(SsqNum num : lastList) {
			String key = num.getN() + "";
			if(result.containsKey(key)) {
				result.put(key, result.get(key) + 1);
			}else {
				result.put(key, 0);
			}
		}
		
		return result;
	}


	/**
	 * 记录分页查询.
	 * @param pageable 分页对象
	 * @return Page<SsqRecord>
	 */
	public Page<SsqRecord> findRecords(Pageable pageable) {
		Page<SsqRecord> page = ssqRecordDao.findAll(pageable);

		return page;
	}
		

	public Map<String, Integer> calcMinorTrend(List<SsqRecord> records,
			MinorTrendProcessor processor) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean exist(Integer[] record) {
		String hql = "select t from SsqRecord t where t.r1 = ? and t.r2 = ? and t.r3 = ? and t.r4 = ? and t.r5 = ? and t.r6 = ?";
		List list = baseDao.find(hql,record);
		return list != null && list.size() > 0;
	}
	
}
