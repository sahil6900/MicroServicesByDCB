package com.sbms.paymentservice.service;

import com.sbms.paymentservice.model.PaymentRequest;
import com.sbms.paymentservice.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetails(long orderId);
}
