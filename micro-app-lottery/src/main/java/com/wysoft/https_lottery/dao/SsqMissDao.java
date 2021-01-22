package com.wysoft.https_lottery.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wysoft.https_lottery.model.SsqMiss;

@Repository
public interface SsqMissDao extends JpaRepository<SsqMiss,String>{
	@Query("select max(t.qh) from SsqMiss t")
	public String uniqueResult();
}
