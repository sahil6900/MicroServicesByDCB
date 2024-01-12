package com.sbms.paymentservice.controller;

import com.sbms.paymentservice.model.PaymentRequest;
import com.sbms.paymentservice.model.PaymentResponse;
import com.sbms.paymentservice.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@Log4j2
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/do")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {
        log.info("Payment request processing with paymentRequest :: {}", paymentRequest);
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest),
                HttpStatus.CREATED);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<PaymentResponse> getPaymentDetails(@PathVariable("id") long orderId) {
        log.info("Payment details for orderId :: {}", orderId);
        return new ResponseEntity<>(paymentService.getPaymentDetails(orderId),
                HttpStatus.OK);
    }
}
