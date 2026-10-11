package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.WalletDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletMapperRepo implements RowMapper<WalletDAO> {
    @Override
    public WalletDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        WalletDAO walletDAO = new WalletDAO();
        walletDAO.setWalletId(rs.getString("id"));
        walletDAO.setUserId(rs.getString("user_id"));
        walletDAO.setAvlBal(rs.getDouble("avl_bal"));
        walletDAO.setLastTransaction(rs.getDouble("last_transaction_details"));
        walletDAO.setUsageRewardPoints(rs.getInt("usage_reward_points"));
        return walletDAO;
    }
}
