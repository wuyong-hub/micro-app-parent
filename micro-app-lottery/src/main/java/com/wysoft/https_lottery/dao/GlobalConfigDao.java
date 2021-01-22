package com.wysoft.https_lottery.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wysoft.https_lottery.model.GlobalConfig;

@Repository
public interface GlobalConfigDao extends JpaRepository<GlobalConfig,Integer> {
	@Query("select c from GlobalConfig c where c.configCode = :configCode and c.validity = 1")
	public List<GlobalConfig> findByConfigCode(@Param("configCode")String configCode);
}
