package com.wysoft.https_lottery.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysoft.https_lottery.dao.BaseDao;
import com.wysoft.https_lottery.dao.GlobalConfigDao;
import com.wysoft.https_lottery.dao.SsqRecordDao;
import com.wysoft.https_lottery.helper.SsqDataHelper;
import com.wysoft.https_lottery.model.GlobalConfig;
import com.wysoft.https_lottery.model.SsqRecord;
import com.wysoft.https_lottery.util.DataUtil;

@Service("ssqDataService")
@Transactional
public class SsqDataService {
	@Autowired
	private Logger log;
	
	@Resource
	private BaseDao baseDao;
	
	@Resource
	private GlobalConfigDao globalConfigDao;
	
	@Resource 
	private SsqRecordDao ssqRecordDao;
	
	public void initDB() {
		/**
		 * [ssq_sum]锟斤拷值锟斤拷锟绞硷拷锟�
		 */
		List<GlobalConfig> cs = globalConfigDao.findByConfigCode("SSQ_DB_INIT_STATUS");
		if(cs == null || cs.size() <= 0){
			return ;
		}
		GlobalConfig initStatus = cs.get(0);
		if(initStatus.getConfigValue().equals("1")){
			System.err.println("锟斤拷锟斤拷要锟截革拷锟斤拷始锟斤拷DB锟斤拷");
			return ;
		}
		
		Integer a[] = {1,2,3,4,5,6,7,8,9,10,11,
				12,13,14,15,16,17,18,19,20,21,22,
				23,24,25,26,27,28,29,30,31,32,33};
		Map<Integer,Integer> result = DataUtil.sumOfmInN(a, 33, 6);
		for(Map.Entry<Integer, Integer> e : result.entrySet()){
			System.out.println(e.getKey() + " = " + e.getValue());
			baseDao.updateBySql("insert into ssq_sum(s,c) values(?,?)", e.getKey(),e.getValue());
		}
		
		initStatus.setConfigValue("1");
		globalConfigDao.save(initStatus);
		
		log.info("END initDB.");
	}

	public int initData() {
		int rowCount = 0;
		List<GlobalConfig> cs = globalConfigDao.findByConfigCode("SSQ_DATA_INIT_STATUS");
		if(cs == null || cs.size() <= 0){
			return rowCount;
		}
		GlobalConfig initStatus = cs.get(0);
		if(initStatus.getConfigValue().equals("1")){
			System.err.println("锟斤拷锟斤拷要锟截革拷锟斤拷始锟斤拷DATA锟斤拷");
			return rowCount;
		}
		
		cs = globalConfigDao.findByConfigCode("SSQ_DATA_URL_EXPR");
		if(cs == null || cs.size() <= 0){
			return rowCount;
		}
		GlobalConfig dataUrl = cs.get(0);
		
		String urlExpr = dataUrl.getConfigValue();
		
		for(int year = 2003; year <= 2015; year ++){
			String url = urlExpr.replace("{YEAR}", "" + year);
			List<SsqRecord> list = SsqDataHelper.getData(url, null, null);
			if(list == null || list.size() <= 0){
				continue;
			}
			
			for(SsqRecord r : list){
				ssqRecordDao.save(r);
			}
			rowCount += list.size();
		}
		
		initStatus.setConfigValue("1");
		globalConfigDao.save(initStatus);
		
		return rowCount;
	}

	@Transactional
	public int syncData() {
		int rowCount = 0;
		List<GlobalConfig> cs = globalConfigDao.findByConfigCode("SSQ_DATA_INIT_STATUS");
		if(cs == null || cs.size() <= 0){
			return rowCount;
		}
		GlobalConfig config = cs.get(0);
		if(config.getConfigValue().equals("0")){
			return initData();
		}
		
		cs = globalConfigDao.findByConfigCode("SSQ_DATA_URL_EXPR");
		if(cs == null || cs.size() <= 0){
			return rowCount;
		}
		GlobalConfig dataUrl = cs.get(0);
		
		String urlExpr = dataUrl.getConfigValue();
		
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String curYear = sdf.format(d);
		
		String url = urlExpr.replace("{YEAR}", curYear);
		
		String beginDate = null;
		List<SsqRecord> list = baseDao.find("select s from SsqRecord s order by s.rq desc", new Object[]{}, null, null);
		if(list == null || list.size() <= 0){
			beginDate = null;
		}
		else{
			SsqRecord r = list.get(0);
			String rq = r.getRq();
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				d = sdf.parse(rq);
				beginDate = sdf.format(new Date(d.getTime() + (long)24 * 60 * 60 * 1000));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		list = SsqDataHelper.getData(url, beginDate, null);
		
		if(list == null || list.size() <= 0){
			return rowCount;
		}
		
		for(SsqRecord r : list){
			ssqRecordDao.save(r);
		}
		
		ssqRecordDao.updateNum();
		return list.size();
	}

}
