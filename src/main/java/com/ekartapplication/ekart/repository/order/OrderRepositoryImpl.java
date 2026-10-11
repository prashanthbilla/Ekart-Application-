package com.ekartapplication.ekart.repository.order;

import com.ekartapplication.ekart.dao.OrderDAO;
import com.ekartapplication.ekart.utils.mapper.OrderMapperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean saveOrder(OrderDAO orderDao) {
        try {
            String sql = "INSERT INTO order_info (order_id, user_id, products, product_count, total_price, " +
                    "discount_price, pay, order_date, user_fullname, user_address, user_mobileNum, payment_status, paid_amount, payment_id, transaction_id, order_status) VALUES (\'"
                    + orderDao.getOrderId() + "\', \'" +
                    orderDao.getUserId() + "\', \'" +
                    orderDao.getProducts() + "\', \'" +
                    orderDao.getProductCount() + "\', \'" +
                    orderDao.getTotalPrice() + "\', \'" +
                    orderDao.getDiscountPrice() + "\', \'" +
                    orderDao.getPay() + "\', \'" +
                    orderDao.getOrderDate() + "\', \'" +
                    orderDao.getUserFullName() + "\', \'" +
                    orderDao.getUserAddress() + "\', \'" +
                    orderDao.getMobileNumber() + "\', \'" +
                    orderDao.getPaymentStatus() + "\', \'" +
                    orderDao.getPaidAmount() + "\', \'" +
                    orderDao.getPaymentId() + "\', \'" +
                    orderDao.getTransactionId() + "\', \'" +
                    orderDao.getOrderStatus() + "\');";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<OrderDAO> getAllOrderDetails() {
        List<OrderDAO> orderDAOList;
        try {
            String sql = "SELECT * FROM order_info";
            orderDAOList = jdbcTemplate.query(sql, new OrderMapperRepo());
            return orderDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void batchUpdateStatuses(List<OrderDAO> updatedOrders) {
        String sql = "UPDATE order_info SET order_status = ?, delivery_date = ? WHERE order_id = ?";

        jdbcTemplate.batchUpdate(sql,
                updatedOrders,
                100, // batch size
                (ps, order) -> {
                    // Set order_status (VARCHAR)
                    ps.setString(1, order.getOrderStatus());

                    // Set delivery_date (VARCHAR, comes from String in Java)
                    String deliveryDate = order.getDeliveryDate(); // this is a String
                    if (deliveryDate != null && !deliveryDate.trim().isEmpty()) {
                        ps.setString(2, deliveryDate);
                    } else {
                        ps.setNull(2, java.sql.Types.VARCHAR);
                    }

                    // Set order_id (VARCHAR)
                    ps.setString(3, order.getOrderId());
                }
        );
    }

    @Override
    public List<OrderDAO> getAllOrderDetailsByUserId(String userId) {
        List<OrderDAO> orderDAOList;
        try {
            String sql = "SELECT * FROM order_info WHERE user_id = \"" + userId + "\";";
            orderDAOList = jdbcTemplate.query(sql, new OrderMapperRepo());
            return orderDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrderDAO> getAllOrderDetailsByOrderDate(Date orderDate) {
        List<OrderDAO> orderDAOList;
        try {
            String sql = "SELECT * FROM order_info WHERE order_date = \"" + orderDate + "\";";
            orderDAOList = jdbcTemplate.query(sql, new OrderMapperRepo());
            return orderDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrderDAO> getAllOrderDetailsByPaymentStatus(String paymentStatus) {
        List<OrderDAO> orderDAOList;
        try {
            String sql = "SELECT * FROM order_info WHERE payment_status = \"" + paymentStatus + "\";";
            orderDAOList = jdbcTemplate.query(sql, new OrderMapperRepo());
            return orderDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrderDAO> getAllOrderDetailsByOrderSatus(String orderStatus) {
        List<OrderDAO> orderDAOList;
        try {
            String sql = "SELECT * FROM order_info WHERE order_status = \"" + orderStatus + "\";";
            orderDAOList = jdbcTemplate.query(sql, new OrderMapperRepo());
            return orderDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public OrderDAO getOrderDetailsByOrderId(String orderId) {
        OrderDAO orderDAO;
        try {
            String sql = "SELECT * FROM order_info WHERE order_id = \"" + orderId + "\";";
            orderDAO = jdbcTemplate.queryForObject(sql, new OrderMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return orderDAO;
    }

    @Override
    public OrderDAO getOrderDetailsByTransactionId(String transactionId) {
        OrderDAO orderDAO;
        try {
            String sql = "SELECT * FROM order_info WHERE transaction_id = \"" + transactionId + "\";";
            orderDAO = jdbcTemplate.queryForObject(sql, new OrderMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return orderDAO;
    }
}
