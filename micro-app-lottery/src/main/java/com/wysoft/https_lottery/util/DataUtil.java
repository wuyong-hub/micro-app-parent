package com.wysoft.https_lottery.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtil {
	/**
	 * ����n��������m�����ֵ�����γɵĺ�ֵ������ִ���
	 * @param arr �� ��ѡ����
	 * @param n ����ѡ���ָ���
	 * @param m : ѡ���ĸ���
	 * @return Map<Integer,Integer> : key->��ֵ,value->���ִ���
	 */
	public static Map<Integer,Integer> sumOfmInN(Integer arr[],int n,int m){
		Map<Integer,Integer> sumMap = new HashMap<Integer,Integer>();
		List<String> result = mInN(arr,n,m,0);
		for(String r : result){
			String []values = r.split(",");
			Integer sum = 0;
			for(int i = 0; i < values.length;i ++){
				sum += Integer.parseInt(values[i]);
			}
			Integer count = sumMap.get(sum);
			if(count != null){
				count ++;
			}
			else {
				count = 1;
			}
			sumMap.put(sum, count);
		}
		return sumMap;
	}
	
	/**
	 * ����n��������ѡ��m�����������
	 * @param arr
	 * @param n
	 * @param m
	 * @param pos
	 * @return
	 */
	public static List<String> mInN(Integer arr[],int n,int m,int pos){
		if(m == 1){
			List<String> result = new ArrayList<String>();
			for(int i = pos; i < n; i ++){
				result.add(arr[i] + "");
			}
			return result;
		}
		
		List<String> all = new ArrayList<String>();
		
		for(int j = pos; j < n - m + 1; j ++){
			Integer cur = arr[j];
			List<String> result = mInN(arr,n,m-1,j+1);
			for(String r : result){
				all.add(cur + "," + r);
			}
		}
		
		return all;
	}
	
	/**
	 * ��s��t������������Ĺ�ͬԪ��
	 * @param s
	 * @param t
	 * @param sharedEls
	 * @return
	 */
	public static int countSharedEl(Integer []s,Integer []t,List<Integer> sharedEls){
		int len = s.length;
		int count = 0;
		
		for(int i = 0; i < len; i ++){
			for(int j = 0; j < len; j ++){
				if(s[i] == t[j]){
					count ++;
					if(sharedEls != null){
						sharedEls.add(s[i]);
					}
					break;
				}
			}
		}
		
		return count;
	}
}
