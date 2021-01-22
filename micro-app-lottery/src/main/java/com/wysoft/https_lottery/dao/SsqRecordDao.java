package com.wysoft.https_lottery.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wysoft.https_lottery.model.SsqRecord;

@Repository
public interface SsqRecordDao extends JpaRepository<SsqRecord,String>,JpaSpecificationExecutor<SsqRecord> {
	@Query(value="update SsqRecord r set r.num = r.r1 + r.r2 + r.r3 + r.r4 + r.r5 + r.r6 where r.num is null")
	@Modifying
	public int updateNum();
}
