package com.ekartapplication.ekart.repository.wallet;

import com.ekartapplication.ekart.dao.WalletDAO;

public interface WalletRepository {
    WalletDAO getWalletDetails(String userId);

    boolean addMoneyToWalletInsert(WalletDAO walletDAO);

    boolean addMoneyToWalletUpdate(WalletDAO walletDAO);

    int isUserExists(String userId);

    void deleteKartByUserId(String userName);
}
