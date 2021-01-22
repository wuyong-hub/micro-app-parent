package com.wysoft.https_lottery.algorithm;

import java.util.List;
import java.util.Map;

import com.wysoft.https_lottery.model.SsqRecord;

public interface MinorTrendProcessor {
	public Map<String,Integer> calcResult(List<SsqRecord> records);
}
