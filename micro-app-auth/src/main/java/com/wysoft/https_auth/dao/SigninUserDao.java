package com.wysoft.https_auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wysoft.https_auth.model.SigninUser;

@Repository
public interface SigninUserDao extends JpaRepository<SigninUser, Integer> {
	@Query(value = "SELECT u FROM SigninUser u WHERE username=:username")
	public SigninUser findByUsername(@Param("username") String username);
}
