package com.ekartapplication.ekart.service.wallet;

import com.ekartapplication.ekart.model.AddMoneyRequest;
import com.ekartapplication.ekart.dao.WalletDAO;

public interface WalletService {
    String addAmountByUserId(String userId, double amount);

    String addAmount(AddMoneyRequest amount);

    WalletDAO checkBalance(String userId);
}
