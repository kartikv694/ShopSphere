package com.Infosys.ecommerceApplication.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // ✅ Add Product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // ✅ Get All Products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ Get Product by ID 
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // ✅ Search Products
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ✅ Get Products by Category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // ✅ NEW: Search by Name + Category
    public List<Product> searchByNameAndCategory(String keyword, String category) {
        return productRepository
                .findByNameContainingIgnoreCaseAndCategory(keyword, category);
    }
}