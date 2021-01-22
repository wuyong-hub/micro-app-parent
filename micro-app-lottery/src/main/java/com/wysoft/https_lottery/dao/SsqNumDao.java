package com.wysoft.https_lottery.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wysoft.https_lottery.model.SsqNum;

@Repository
public interface SsqNumDao extends JpaRepository<SsqNum,Integer> {

}
