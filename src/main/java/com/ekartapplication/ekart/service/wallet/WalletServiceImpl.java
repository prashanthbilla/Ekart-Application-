package com.ekartapplication.ekart.service.wallet;

import com.ekartapplication.ekart.model.AddMoneyRequest;
import com.ekartapplication.ekart.dao.WalletDAO;
import com.ekartapplication.ekart.repository.wallet.WalletRepository;
import com.ekartapplication.ekart.service.user.UserService;
import com.ekartapplication.ekart.utils.IDGenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    WalletRepository repository;

    @Autowired
    UserService userService;

    @Override
    public String addAmountByUserId(String userId, double amount) {
        return null;
    }

    @Override
    public String addAmount(AddMoneyRequest amount) {
        WalletDAO walletDAO;
        boolean inserted;
        boolean isUserExistsInWallet = isUserExists(amount.getUserId());
        boolean isUserExists = userService.isUserExistsByUserName(amount.getUserId());
        if (!isUserExists)
            return "User not found...";
        walletDAO = repository.getWalletDetails(amount.getUserId());
        int addingRewardPoints;
        if (!isUserExistsInWallet && walletDAO == null) {
            walletDAO = new WalletDAO();
            walletDAO.setWalletId(IDGenUtil.getUniqueAlphaNumericString(8));
            walletDAO.setUserId(amount.getUserId());
            walletDAO.setAvlBal(amount.getAmount());
            addingRewardPoints = (walletDAO.getUsageRewardPoints() + ((int) (amount.getAmount() / 100)));
            walletDAO.setLastTransaction(amount.getAmount());
            walletDAO.setUsageRewardPoints(addingRewardPoints);
            inserted = repository.addMoneyToWalletInsert(walletDAO);
        } else {
            walletDAO.setAvlBal((walletDAO.getAvlBal() + amount.getAmount()));
            walletDAO.setUserId(amount.getUserId());
            addingRewardPoints = (walletDAO.getUsageRewardPoints() + ((int) (amount.getAmount() / 100)));
            walletDAO.setLastTransaction(amount.getAmount());
            walletDAO.setUsageRewardPoints(addingRewardPoints);
            inserted = repository.addMoneyToWalletUpdate(walletDAO);

        }
        if (!inserted)
            return "Amount not Added";
        return "Amount Added successfully, last transaction amount = " + amount.getAmount() + ", Available Bal = " + walletDAO.getAvlBal();
    }

    @Override
    public WalletDAO checkBalance(String userId) {
        return repository.getWalletDetails(userId);
    }

    public boolean isUserExists(String userId) {
        int count = repository.isUserExists(userId);
        return count == 1;
    }

}
