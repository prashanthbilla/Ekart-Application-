package com.ekartapplication.ekart.repository.order;

import com.ekartapplication.ekart.dao.OrderDAO;

import java.sql.Date;
import java.util.List;

public interface OrderRepository {
    boolean saveOrder(OrderDAO orderDao);

    List<OrderDAO> getAllOrderDetails();

    List<OrderDAO> getAllOrderDetailsByUserId(String userId);

    List<OrderDAO> getAllOrderDetailsByOrderDate(Date orderDate);

    List<OrderDAO> getAllOrderDetailsByPaymentStatus(String paymentStatus);

    List<OrderDAO> getAllOrderDetailsByOrderSatus(String orderStatus);

    OrderDAO getOrderDetailsByOrderId(String orderId);

    OrderDAO getOrderDetailsByTransactionId(String transactionId);

    void batchUpdateStatuses(List<OrderDAO> updatedOrders);
}
