package com.sbms.productservice.service;

import com.sbms.productservice.model.ProductRequest;
import com.sbms.productservice.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
