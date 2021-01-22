package com.wysoft.https_base.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class JdbcDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Map<String,Object>> queryForList(String sql,Object... args){
		return jdbcTemplate.queryForList(sql, args);
	}
	
	public <T> List<T> queryForList(String sql,RowMapper<T> rowMapper,Object... args){
		return jdbcTemplate.query(sql, args, rowMapper);
	}
	
	public Map<String,Object> queryForMap(String sql,Object... args){
		return jdbcTemplate.queryForMap(sql, args);
	}
	
	@Transactional
	public int update(String sql,Object... args){
		return jdbcTemplate.update(sql, args);
	}
	
	@Transactional
	public int[] batchUpdate(String sql,List<Object[]> batchArgs){
		return jdbcTemplate.batchUpdate(sql, batchArgs);
	}
}
