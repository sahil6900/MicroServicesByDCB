package com.sbms.paymentservice.exception;

import lombok.Data;

@Data
public class PaymentServiceCustomException extends RuntimeException{
    private String errorCode;
    private int status;

    public PaymentServiceCustomException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
