package com.ekartapplication.ekart.utils.mapper;

import com.ekartapplication.ekart.dao.OrderDAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapperRepo implements RowMapper<OrderDAO> {
    @Override
    public OrderDAO mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderDAO orderDAO = new OrderDAO();
        orderDAO.setOrderId(rs.getString("order_id"));
        orderDAO.setUserId(rs.getString("user_id"));
        orderDAO.setProducts(rs.getString("products"));
        orderDAO.setProductCount(rs.getInt("product_count"));
        orderDAO.setTotalPrice(rs.getDouble("total_price"));
        orderDAO.setDiscountPrice(rs.getDouble("discount_price"));
        orderDAO.setPay(rs.getDouble("pay"));
        orderDAO.setOrderDate(rs.getDate("order_date"));
        orderDAO.setUserFullName(rs.getString("user_fullname"));
        orderDAO.setUserAddress(rs.getString("user_address"));
        orderDAO.setMobileNumber(rs.getString("user_mobileNum"));
        orderDAO.setPaymentStatus(rs.getString("payment_status"));
        orderDAO.setPaidAmount(rs.getDouble("paid_amount"));
        orderDAO.setPaymentId(rs.getString("payment_id"));
        orderDAO.setTransactionId(rs.getString("transaction_id"));
        orderDAO.setOrderStatus(rs.getString("order_status"));
        orderDAO.setDeliveryDate(rs.getString("delivery_date"));
        return orderDAO;
    }
}
