package com.wysoft.https_auth.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wysoft.https_auth.model.Product;

@Repository
public interface ProductDao extends JpaRepository<Product, String> {

}
