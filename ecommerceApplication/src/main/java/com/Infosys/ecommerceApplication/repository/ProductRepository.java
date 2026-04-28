package com.Infosys.ecommerceApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByCategory(String category);
    
    List<Product> findByNameContainingIgnoreCaseAndCategory(String keyword, String category);
}