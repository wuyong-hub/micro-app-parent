package com.wysoft.https_auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wysoft.https_auth.model.UaamAuthLogin;

public interface AuthLoginDao extends JpaRepository<UaamAuthLogin,String>{
	
}
