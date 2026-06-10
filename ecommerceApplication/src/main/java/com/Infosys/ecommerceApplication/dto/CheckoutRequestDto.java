package com.Infosys.ecommerceApplication.dto;

import java.util.List;
import java.util.Map;

public class CheckoutRequestDto {

    private String address;

    private String paymentMethod;

    private Map<String, Object> paymentDetails;

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

    public Map<String, Object> getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(Map<String, Object> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public List<CartItemDto> getItems() {
        return items;
    }

    public void setItems(List<CartItemDto> items) {
        this.items = items;
    }

}
