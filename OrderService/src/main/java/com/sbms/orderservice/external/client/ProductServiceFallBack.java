package com.sbms.orderservice.external.client;

import com.sbms.orderservice.exception.OrderServiceCustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("productService")
public class ProductServiceFallBack implements ProductService{
    @Override
    public ResponseEntity<Void> reduceQuantity(long productId, long quantity) {
        throw new OrderServiceCustomException("Product Service is down...!",
                "UNAVAILABLE",500);
    }
}
