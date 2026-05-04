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

    // ADD
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // GET ALL
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // GET BY ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // DELETE
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // SEARCH
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // CATEGORY
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    // ADV SEARCH
    public List<Product> searchByNameAndCategory(String keyword, String category) {
        return productRepository
                .findByNameContainingIgnoreCaseAndCategory(keyword, category);
    }
}