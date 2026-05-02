package com.Infosys.ecommerceApplication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.repository.ProductRepository;
import com.Infosys.ecommerceApplication.repository.userRepository;

@Service
public class DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private userRepository userRepository;

    public long getTotalProducts() {
        return productRepository.count();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }
}