package com.sbms.orderservice.external.client;

import com.sbms.orderservice.exception.OrderServiceCustomException;
import com.sbms.orderservice.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/payment", fallback = PaymentServiceFallBack.class)
@Component("paymentService")
public interface PaymentService {

    @PostMapping("/do")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

}
