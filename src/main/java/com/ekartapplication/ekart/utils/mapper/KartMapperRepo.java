package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.KartDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KartMapperRepo  implements RowMapper<KartDAO> {
    @Override
    public KartDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        KartDAO kartDAO = new KartDAO();
        kartDAO.setKartId(rs.getString("kart_id"));
        kartDAO.setUserId(rs.getString("user_id"));
        kartDAO.setProducts(rs.getString("products"));
        kartDAO.setTotalProducts(rs.getInt("product_count"));
        kartDAO.setTotalProductsPrice(rs.getDouble("total_price"));
        kartDAO.setDiscountPrice(rs.getDouble("discount_price"));
        kartDAO.setPayTotal(rs.getDouble("pay"));
        kartDAO.setEarnRewardPoints(rs.getInt("get_reward_points"));
        return kartDAO;
    }
}
