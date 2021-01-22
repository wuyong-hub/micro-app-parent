package com.wysoft.https_auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wysoft.https_auth.model.UaamUser;

@Repository
public interface UserDao extends JpaRepository<UaamUser, Integer> {
	@Query(value = "SELECT u FROM UaamUser u WHERE username=:username")
	public UaamUser findByUsername(@Param("username") String username);

}
