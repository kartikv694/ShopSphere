package com.Infosys.ecommerceApplication.dto;

import java.util.List;
import java.util.Map;

public class PaymentOrderRequest {

    private String gateway;
    private double amount;
    private String currency;
    private String paymentMethod;
    private Map<String, String> customer;
    private String address;
    private List<CartItemDto> items;

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Map<String, String> getCustomer() {
        return customer;
    }

    public void setCustomer(Map<String, String> customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }
}
