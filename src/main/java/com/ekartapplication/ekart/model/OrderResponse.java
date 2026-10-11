package com.ekartapplication.ekart.model;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderResponse {

    private String orderId;
    private String userId;
    private String products;
    private int productCount;
    private double totalPrice;
    private double discountPrice;
    private double pay;
    private Date orderDate;
    private String userFullName;
    private String mobileNumber;
    private String paymentStatus;
    private double paidAmount;
    private String paymentId;
    private String transactionId;
    private String orderStatus;
    private String deliveryDate;
    private UserAddress userAddress;

}
