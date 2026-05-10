package com.Infosys.ecommerceApplication.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Infosys.ecommerceApplication.model.Order;
import com.Infosys.ecommerceApplication.model.User;


public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(User customer);

}