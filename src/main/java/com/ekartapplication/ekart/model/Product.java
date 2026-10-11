package com.ekartapplication.ekart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    private String productId;

    private String productName;
    private String categoryId;
    private String productImageUrl;
    private String productDetails;
    private double actualPrice;
    private int offerPercentage;
    private int usageRewardPoints;
}
