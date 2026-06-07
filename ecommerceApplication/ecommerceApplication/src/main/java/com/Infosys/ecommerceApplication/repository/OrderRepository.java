package com.Infosys.ecommerceApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import com.Infosys.ecommerceApplication.model.Order;
import com.Infosys.ecommerceApplication.model.User;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "customer"})
    List<Order> findByCustomer(User customer);

    @Override
    @EntityGraph(attributePaths = {"orderItems", "orderItems.product", "customer"})
    List<Order> findAll();

}
