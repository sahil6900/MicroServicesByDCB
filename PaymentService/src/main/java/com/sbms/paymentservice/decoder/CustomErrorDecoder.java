package com.sbms.paymentservice.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbms.paymentservice.exception.PaymentServiceCustomException;
import com.sbms.paymentservice.response.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {

        ObjectMapper objectMapper = new ObjectMapper();

        log.info("::{}",response.request().url());
        log.info("::{}",response.request().headers());
        log.info("::{}",response.request().body());

        try {
            ErrorResponse errorResponse =
                    objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
            throw new PaymentServiceCustomException(errorResponse.getErrorMessage(),
                    errorResponse.getErrorCode(), response.status());
        }catch (IOException exception){
            throw new PaymentServiceCustomException("Internal Server error"
            ,"INTERNAL_SERVER_ERROR"
            ,500);
        }
    }
}
