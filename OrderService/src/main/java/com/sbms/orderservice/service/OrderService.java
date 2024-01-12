package com.sbms.orderservice.service;

import com.sbms.orderservice.model.OrderRequest;
import com.sbms.orderservice.model.OrderResponse;

public interface OrderService {
    Long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(Long orderId);
}
