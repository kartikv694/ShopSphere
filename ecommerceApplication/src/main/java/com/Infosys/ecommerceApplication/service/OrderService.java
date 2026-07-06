package com.Infosys.ecommerceApplication.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private static final Set<String> ALLOWED_STATUSES =
            Set.of("PLACED", "SHIPPED", "DELIVERED");

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

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

        return orderRepository.findByCustomerOrderByIdDesc(customer);

    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    @Transactional
    public Order updateOrderStatus(
            Long orderId,
            String status
    ) {
        String normalizedStatus =
                normalizeStatus(status);

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Order not found"
                                )
                        );

        order.setStatus(normalizedStatus);

        Order savedOrder =
                orderRepository.save(order);

        if ("SHIPPED".equals(normalizedStatus)) {
            scheduleDeliveryUpdate(orderId);
        }

        return savedOrder;
    }

    private String normalizeStatus(String status) {
        String normalizedStatus =
                status == null
                        ? ""
                        : status
                                .trim()
                                .toUpperCase()
                                .replace(" ", "_")
                                .replace("-", "_");

        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            throw new RuntimeException("Invalid order status");
        }

        return normalizedStatus;
    }

    private void scheduleDeliveryUpdate(Long orderId) {
        scheduler.schedule(
                () -> {
                    try {
                        Order latestOrder =
                                orderRepository
                                        .findById(orderId)
                                        .orElse(null);

                        if (
                                latestOrder != null &&
                                "SHIPPED".equals(
                                        latestOrder.getStatus()
                                )
                        ) {
                            latestOrder.setStatus("DELIVERED");
                            orderRepository.save(latestOrder);
                        }
                    } catch (Exception ignored) {
                    }
                },
                30,
                TimeUnit.SECONDS
        );
    }

}
