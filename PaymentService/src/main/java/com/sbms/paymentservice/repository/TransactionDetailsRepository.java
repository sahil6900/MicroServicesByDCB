package com.sbms.paymentservice.repository;

import com.sbms.paymentservice.entity.TransactionDetails;
import com.sbms.paymentservice.exception.PaymentServiceCustomException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails,Long> {
    TransactionDetails findByOrderId(long orderId) throws PaymentServiceCustomException;
}
