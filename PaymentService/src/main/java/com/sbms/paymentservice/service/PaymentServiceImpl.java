package com.sbms.paymentservice.service;

import com.sbms.paymentservice.entity.TransactionDetails;
import com.sbms.paymentservice.exception.PaymentServiceCustomException;
import com.sbms.paymentservice.model.PaymentMode;
import com.sbms.paymentservice.model.PaymentRequest;
import com.sbms.paymentservice.model.PaymentResponse;
import com.sbms.paymentservice.repository.TransactionDetailsRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Override
    public Long doPayment(PaymentRequest paymentRequest) {
        log.info("Recording payment Details :: {}", paymentRequest);
        TransactionDetails details = TransactionDetails
                .builder()
                .orderId(paymentRequest.getOrderId())
                .paymentDate(Instant.now())
                .paymentMode(paymentRequest.getPaymentMode().name())
                .paymentStatus("SUCCESS")
                .amount(paymentRequest.getAmount())
                .refernceNumber(paymentRequest.getRefernceNumber())
                .build();

        transactionDetailsRepository.save(details);

        log.info("Transaction completed with ID :: {}", details.getId());

        return details.getId();
    }

    @Override
    public PaymentResponse getPaymentDetails(long orderId) {
        log.info("Getting payment details for the orderId :: {}",orderId);

        TransactionDetails details = null;
        PaymentResponse paymentResponse = null;
        try {
            details = transactionDetailsRepository.findByOrderId(Long.valueOf(orderId));
            log.info("Transaction details found: {}", details);  // Log details result

            if (details == null) {
                log.error("Payment not found with given orderId :: {}", orderId);
                throw new PaymentServiceCustomException("No payment found with given id :: " + orderId,
                        "NOT_FOUND", 400);
            }else {
                paymentResponse = PaymentResponse
                        .builder()
                        .paymentId(details.getId())
                        .paymentMode(PaymentMode.valueOf(details.getPaymentMode()))
                        .paymentDate(details.getPaymentDate())
                        .status(details.getPaymentStatus())
                        .amount(details.getAmount())
                        .build();
            }

        }catch (Exception exception){
            log.error("Exception occurred :: {}", exception.getMessage());
            throw new PaymentServiceCustomException(exception.getMessage(), "NOT_FOUND", 500);
        }


        return paymentResponse;
    }
}
