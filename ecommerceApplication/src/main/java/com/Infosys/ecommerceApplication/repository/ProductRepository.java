package com.Infosys.ecommerceApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Infosys.ecommerceApplication.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	
}