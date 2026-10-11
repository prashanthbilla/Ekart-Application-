package com.ekartapplication.ekart.service.order;

import com.ekartapplication.ekart.dao.OrderDAO;
import com.ekartapplication.ekart.model.OrderResponse;
import com.ekartapplication.ekart.utils.enums.OrderFilterMode;
import com.ekartapplication.ekart.utils.enums.OrderFilterSingle;

import java.sql.Date;
import java.util.List;

public interface OrderService {
    List<OrderResponse> getOrderDetailsAll(OrderFilterMode mode, String userId, Date orderDate, String paymentStatus, String orderStatus);

    OrderResponse getOrderDetailsByIds(OrderFilterSingle mode, String orderId, String transactionId);
}
