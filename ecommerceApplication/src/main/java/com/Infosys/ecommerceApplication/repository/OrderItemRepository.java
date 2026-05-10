package com.Infosys.ecommerceApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.OrderItem;


public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

}