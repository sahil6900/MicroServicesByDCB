package com.sbms.productservice.service;

import com.sbms.productservice.entity.Product;
import com.sbms.productservice.exception.ProductServiceCustomException;
import com.sbms.productservice.model.ProductRequest;
import com.sbms.productservice.model.ProductResponse;
import com.sbms.productservice.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Override
    public long addProduct(ProductRequest productRequest) {

        Product product = Product.builder()
                .productName(productRequest.getName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();

        productRepository.save(product);

        log.info("Product created {} :: ",product);

        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {

        log.info("Getting product with productId :: {} ",productId);
        Product product = productRepository.findById(productId).orElseThrow(()->{

            log.info("Product with given id {} not found",productId);

            throw new ProductServiceCustomException("product with given id " + productId + "not found","PRODUCT_NOT_FOUND");

        });
//        return ProductResponse
//                .builder()
//                .productName(product.getProductName())
//                .price(product.getPrice())
//                .quantity(product.getQuantity())
//                .build();

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product,productResponse);

        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce Quantity :: {} for ProductId :: {} ",quantity,productId);

        Product product = productRepository.findById(productId).orElseThrow(
                ()->new ProductServiceCustomException("Product with given productId " + productId + "not found"
                ,"PRODUCT_NOT_FOUND")
        );

        if(product.getQuantity()<quantity){
            log.info("Product does not have sufficient quantity -> Quantity left is :: {}",product.getQuantity());
            throw new ProductServiceCustomException("Product does not have sufficient quantity"
            ,"INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity()-quantity);

        productRepository.save(product);

        log.info("Product updated successfully");
    }
}
