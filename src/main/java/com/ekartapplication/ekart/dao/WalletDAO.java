package com.ekartapplication.ekart.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletDAO {
    private String walletId;
    private String userId;
    private double avlBal;
    private double lastTransaction;
    private int usageRewardPoints;
}
