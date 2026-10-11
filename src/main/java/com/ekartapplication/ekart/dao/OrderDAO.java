package com.ekartapplication.ekart.dao;

import lombok.*;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class OrderDAO {

    private String orderId;
    private String userId;
    private String products;
    private int productCount;
    private double totalPrice;
    private double discountPrice;
    private double pay;
    private Date orderDate;
    private String userFullName;
    private String userAddress;
    private String mobileNumber;
    private String paymentStatus;
    private double paidAmount;
    private String paymentId;
    private String transactionId;
    private String orderStatus;
    private String deliveryDate;
}
