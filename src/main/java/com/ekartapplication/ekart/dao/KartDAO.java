package com.ekartapplication.ekart.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KartDAO {
    private String kartId;
    private String userId;
    private String products;
    private double totalProductsPrice;
    private int totalProducts;
    private double discountPrice;
    private double payTotal;
    private int earnRewardPoints;
}
