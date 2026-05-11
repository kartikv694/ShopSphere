package com.Infosys.ecommerceApplication.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Infosys.ecommerceApplication.dto.CartItemDto;
import com.Infosys.ecommerceApplication.dto.CheckoutRequestDto;
import com.Infosys.ecommerceApplication.model.Order;
import com.Infosys.ecommerceApplication.model.OrderItem;
import com.Infosys.ecommerceApplication.model.Product;
import com.Infosys.ecommerceApplication.model.User;
import com.Infosys.ecommerceApplication.repository.OrderRepository;
import com.Infosys.ecommerceApplication.repository.ProductRepository;
import com.Infosys.ecommerceApplication.repository.userRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private userRepository userRepository;

    @Transactional
    public Order checkout(
            CheckoutRequestDto request,
            String email
    ) {

        // GET USER
        User customer =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found"
                                        )
                        );

        // CREATE ORDER
        Order order = new Order();

        order.setCustomer(customer);

        order.setAddress(
                request.getAddress()
        );

        order.setPaymentMethod(
                request.getPaymentMethod()
        );

        order.setOrderDate(
                LocalDateTime.now()
        );

        order.setStatus("PLACED");

        // ORDER ITEMS
        List<OrderItem> orderItems =
                new ArrayList<>();

        double totalPrice = 0;

        for (
                CartItemDto item
                        : request.getItems()
        ) {

            Product product =
                    productRepository
                            .findById(
                                    item.getProductId()
                            )
                            .orElseThrow(
                                    () ->
                                            new RuntimeException(
                                                    "Product not found"
                                            )
                            );

            double subtotal =
                    product.getPrice()
                            * item.getQuantity();

            totalPrice += subtotal;

            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(product);

            orderItem.setQuantity(
                    item.getQuantity()
            );

            orderItem.setPrice(
                    product.getPrice()
            );

            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

        }

        order.setOrderItems(orderItems);

        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);

    }

    public List<Order> getCustomerOrders(
            String email
    ) {

        User customer =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "User not found"
                                        )
                        );

        return orderRepository.findByCustomer(customer);

    }

}