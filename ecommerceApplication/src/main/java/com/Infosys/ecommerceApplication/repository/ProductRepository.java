package com.Infosys.ecommerceApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // 🔍 Search by product name
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // 📂 Category filter
    List<Product> findByCategoryIgnoreCase(String category);

    // 🔥 Search + Category filter
    List<Product> findByNameContainingIgnoreCaseAndCategoryIgnoreCase(
            String keyword,
            String category
    );
}