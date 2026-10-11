package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KartDetailsResponse {
    private String kartId;
    private String userId;
    private int productCount;
    private double totalPrice;
    private double discountPrice;
    private double pay;
    private int getRewardPoints;
    private Map<String,Integer> products;
}
