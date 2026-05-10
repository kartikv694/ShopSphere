package com.Infosys.ecommerceApplication.dto;

import java.util.List;

public class CheckoutRequestDto {

    private String address;

    private String paymentMethod;

    private List<CartItemDto> items;

    public CheckoutRequestDto() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

}