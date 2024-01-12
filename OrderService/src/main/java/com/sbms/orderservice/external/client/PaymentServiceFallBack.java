package com.sbms.orderservice.external.client;

import com.sbms.orderservice.exception.OrderServiceCustomException;
import com.sbms.orderservice.external.request.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("paymentService")
public class PaymentServiceFallBack implements PaymentService{
    @Override
    public ResponseEntity<Long> doPayment(PaymentRequest paymentRequest) {
        throw new OrderServiceCustomException("Payment Service is down",
                "UNAVAILABLE",500);
    }
}
