package com.ekartapplication.ekart.repository.wallet;

import com.ekartapplication.ekart.utils.mapper.WalletMapperRepo;
import com.ekartapplication.ekart.dao.WalletDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    @Autowired
    JdbcTemplate template;

    @Override
    public WalletDAO getWalletDetails(String userId) {
        WalletDAO walletDAO;
        try {
            String sql = "SELECT * FROM wallet_info WHERE user_id = \"" + userId
                    + "\";";
            walletDAO = template.queryForObject(sql, new WalletMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return walletDAO;

    }

    @Override
    public boolean addMoneyToWalletInsert(WalletDAO walletDAO) {
        try {
            String sql = "INSERT INTO wallet_info (id, user_id, avl_bal, last_transaction_details, usage_reward_points) VALUES (" +
                    "\'" + walletDAO.getWalletId() +
                    "\', \'" + walletDAO.getUserId() +
                    "\', \'" + walletDAO.getAvlBal() +
                    "\', \'" + walletDAO.getLastTransaction() +
                    "\', \'" + walletDAO.getUsageRewardPoints() +
                    "\');";
            int response = template.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addMoneyToWalletUpdate(WalletDAO walletDAO) {
        try {
            String sql = "UPDATE wallet_info SET avl_bal = \'"
                    + walletDAO.getAvlBal() + "\',last_transaction_details = \'" + walletDAO.getLastTransaction() +
                    "\',usage_reward_points = \'" + walletDAO.getUsageRewardPoints() +
                    "\' where user_id = \'" + walletDAO.getUserId() + "\';";
            int response = template.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int isUserExists(String userId) {
        try {
            return template
                    .queryForObject("select count(user_id) from wallet_info where user_id = ? ", new Object[]{userId}, int.class);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void deleteKartByUserId(String userName) {
        String sql = "DELETE from wallet_info where user_id = \"" + userName + "\"";
        int response = template.update(sql);
    }
}
