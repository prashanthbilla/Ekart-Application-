package com.ekartapplication.ekart.repository.payment;

import com.ekartapplication.ekart.dao.PaymentDAO;
import com.ekartapplication.ekart.utils.mapper.PaymentMapperRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public boolean savePayment(PaymentDAO paymentDAO) {
        try {
            String sql = "INSERT INTO payment_info (payment_id, transaction_id, order_id, user_id, user_fullname, payment_status, " +
                    "paid_amount, total_price, discount_price, products, total_products, payment_mode, payment_date) VALUES (\'"
                    + paymentDAO.getPaymentId() + "\', \'" +
                    paymentDAO.getTransactionId() + "\', \'" +
                    paymentDAO.getOrderId() + "\', \'" +
                    paymentDAO.getUserId() + "\', \'" +
                    paymentDAO.getUserFullName() + "\', \'" +
                    paymentDAO.getPaymentStatus() + "\', \'" +
                    paymentDAO.getPaidAmount() + "\', \'" +
                    paymentDAO.getTotalPrice() + "\', \'" +
                    paymentDAO.getDiscountPrice() + "\', \'" +
                    paymentDAO.getProducts() + "\', \'" +
                    paymentDAO.getTotalProducts() + "\', \'" +
                    paymentDAO.getPaymentMode() + "\', \'" +
                    paymentDAO.getPaymentDate() + "\');";
            int response = jdbcTemplate.update(sql);
            return response == 1;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<PaymentDAO> getAllPaymentDetails() {
        List<PaymentDAO> paymentDAOList;
        try {
            String sql = "SELECT * FROM Payment_info";
            paymentDAOList = jdbcTemplate.query(sql, new PaymentMapperRepo());
            return paymentDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PaymentDAO> getAllPaymentDetailsByUserId(String userId) {
        List<PaymentDAO> paymentDAOList;
        try {
            String sql = "SELECT * FROM Payment_info WHERE user_id = \"" + userId + "\";";
            paymentDAOList = jdbcTemplate.query(sql, new PaymentMapperRepo());
            return paymentDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PaymentDAO> getAllPaymentDetailsByPaymentStatus(String paymentStatus) {
        List<PaymentDAO> paymentDAOList;
        try {
            String sql = "SELECT * FROM Payment_info WHERE payment_status = \"" + paymentStatus + "\";";
            paymentDAOList = jdbcTemplate.query(sql, new PaymentMapperRepo());
            return paymentDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<PaymentDAO> getAllPaymentDetailsByPPaymentMode(String paymentMode) {
        List<PaymentDAO> paymentDAOList;
        try {
            String sql = "SELECT * FROM Payment_info WHERE payment_mode = \"" + paymentMode + "\";";
            paymentDAOList = jdbcTemplate.query(sql, new PaymentMapperRepo());
            return paymentDAOList;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PaymentDAO getPaymentDetailsByOrderId(String orderId) {
        PaymentDAO paymentDAO;
        try {
            String sql = "SELECT * FROM payment_info WHERE order_id = \"" + orderId + "\";";
            paymentDAO = jdbcTemplate.queryForObject(sql, new PaymentMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return paymentDAO;
    }

    @Override
    public PaymentDAO getPaymentDetailsByPaymentId(String paymentId) {
        PaymentDAO paymentDAO;
        try {
            String sql = "SELECT * FROM payment_info WHERE payment_id = \"" + paymentId + "\";";
            paymentDAO = jdbcTemplate.queryForObject(sql, new PaymentMapperRepo());
        } catch (Exception e) {
            return null;
        }
        return paymentDAO;
    }
}
