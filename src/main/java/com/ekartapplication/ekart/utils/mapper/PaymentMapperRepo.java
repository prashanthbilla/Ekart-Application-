package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.PaymentDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentMapperRepo implements RowMapper<PaymentDAO> {
    @Override
    public PaymentDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        PaymentDAO paymentDAO = new PaymentDAO();
        paymentDAO.setPaymentId(rs.getString("payment_id"));
        paymentDAO.setTransactionId(rs.getString("transaction_id"));
        paymentDAO.setOrderId(rs.getString("order_id"));
        paymentDAO.setUserId(rs.getString("user_id"));
        paymentDAO.setUserFullName(rs.getString("user_fullname"));
        paymentDAO.setPaymentStatus(rs.getString("payment_status"));
        paymentDAO.setPaidAmount(rs.getDouble("paid_amount"));
        paymentDAO.setTotalPrice(rs.getDouble("total_price"));
        paymentDAO.setDiscountPrice(rs.getDouble("discount_price"));
        paymentDAO.setProducts(rs.getString("products"));
        paymentDAO.setTotalProducts(rs.getInt("total_products"));
        paymentDAO.setPaymentMode(rs.getString("payment_mode"));
        paymentDAO.setPaymentDate(rs.getDate("payment_date"));
        return paymentDAO;
    }
}
