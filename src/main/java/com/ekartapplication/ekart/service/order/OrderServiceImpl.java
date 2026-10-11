package com.ekartapplication.ekart.service.order;

import com.ekartapplication.ekart.dao.OrderDAO;
import com.ekartapplication.ekart.dao.UserDAO;
import com.ekartapplication.ekart.model.OrderResponse;
import com.ekartapplication.ekart.repository.order.OrderRepository;
import com.ekartapplication.ekart.repository.user.UserRepository;
import com.ekartapplication.ekart.utils.enums.OrderFilterMode;
import com.ekartapplication.ekart.utils.enums.OrderFilterSingle;
import com.ekartapplication.ekart.utils.enums.OrderStatus;
import com.ekartapplication.ekart.utils.mapper.OrderDaoToOrderResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Override
    public List<OrderResponse> getOrderDetailsAll(OrderFilterMode mode, String userId, Date orderDate, String paymentStatus, String orderStatus) {
        List<OrderDAO> orderDAOList = switch (mode) {
            case NONE -> orderRepository.getAllOrderDetails();
            case USER_ID -> orderRepository.getAllOrderDetailsByUserId(userId);
            case ORDER_DATE -> orderRepository.getAllOrderDetailsByOrderDate(orderDate);
            case PAYMENT_STATUS -> orderRepository.getAllOrderDetailsByPaymentStatus(paymentStatus);
            case ORDER_STATUS -> orderRepository.getAllOrderDetailsByOrderSatus(orderStatus);
        };
        if (Objects.isNull(orderDAOList))
            return new ArrayList<>();
        return new OrderDaoToOrderResponseMapper().orderDaoToOrderResponse(orderDAOList);
    }

    @Override
    public OrderResponse getOrderDetailsByIds(OrderFilterSingle mode, String orderId, String transactionId) {
        OrderDAO orderDAO = switch (mode) {
            case ORDER_ID -> orderRepository.getOrderDetailsByOrderId(orderId);
            case TRANSACTION_ID -> orderRepository.getOrderDetailsByTransactionId(transactionId);
        };
        if (Objects.isNull(orderDAO))
            return null;
        return new OrderDaoToOrderResponseMapper().daoToResponseOne(orderDAO);

    }


    // Run every 5 minutes (fixedRate = 5 * 60 * 1000 ms)
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void orderStatusChange() {

        List<OrderDAO> allOrderDetails = orderRepository.getAllOrderDetails();

        LocalDate today = LocalDate.now();

        List<OrderDAO> updatedOrderDetails = allOrderDetails.stream()
                .map(order -> {
                    long days = ChronoUnit.DAYS.between(order.getOrderDate().toLocalDate(), today);
                    String[] deliveryOrder = getDeliveryDetails(days);

                    // Only add to update list if status is changing
                    if (!order.getOrderStatus().equals(deliveryOrder[0]) && !(order.getOrderStatus().equals("DELIVERED"))) {
                        order.setOrderStatus(deliveryOrder[0]);
                        order.setDeliveryDate(deliveryOrder[1]);
                        return order;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        if (!updatedOrderDetails.isEmpty()) {
            orderRepository.batchUpdateStatuses(updatedOrderDetails);
            System.out.println("✅ Updated " + updatedOrderDetails.size() + " orders at " + today);
        } else {
            System.out.println("⏳ No order updates needed at " + today);
        }
    }

    private  String[] getDeliveryDetails(long days) {
        String newStatus;
        String expectedDeliveryDate;
        if (days == 0) {
            newStatus = OrderStatus.ORDERED.name();
            expectedDeliveryDate = deliveryExpectedDate(3);
        } else if (days == 1) {
            newStatus = OrderStatus.DISPATCHED.name();
            expectedDeliveryDate = deliveryExpectedDate(2);
        } else if (days == 2) {
            newStatus = OrderStatus.WAITING_FOR_DELIVERY.name();
            expectedDeliveryDate = deliveryExpectedDate(1);
        } else {
            newStatus = OrderStatus.DELIVERED.name();
            expectedDeliveryDate = Date.valueOf(LocalDate.now()).toString();
        }
        String[] deliveryDetails = new String[2];
        deliveryDetails[0] = newStatus;
        deliveryDetails[1] = expectedDeliveryDate;
        return deliveryDetails;
    }

    private String deliveryExpectedDate(long days) {
        LocalDate localDate = LocalDate.now().plusDays(3 - days);
        Date sqlDate = Date.valueOf(localDate);
        if(days<3)
            return "Delivery expected by " + sqlDate;
        else
            return sqlDate.toString();
    }

}
