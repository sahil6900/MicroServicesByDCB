package com.sbms.orderservice.service;

import com.sbms.orderservice.entity.Order;
import com.sbms.orderservice.exception.OrderServiceCustomException;
import com.sbms.orderservice.external.client.PaymentService;
import com.sbms.orderservice.external.client.ProductService;
import com.sbms.orderservice.external.request.PaymentRequest;
import com.sbms.orderservice.external.response.PaymentResponse;
import com.sbms.orderservice.model.OrderRequest;
import com.sbms.orderservice.model.OrderResponse;
import com.sbms.orderservice.repository.OrderRespository;
import com.sbms.productservice.model.ProductResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRespository orderRespository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Long placeOrder(OrderRequest orderRequest) {

        // create Order Entity -> Save the data with status as order created
        //Product Service -> Block product (Reduce quantity)
        //Payment Service -> Payment -> Success -> Completed
        // Else mark as cancelled

        log.info("Placing Order request :: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status as CREATED");

        Order order = Order
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRespository.save(order);

        log.info("Calling payment service to complete payments");

        PaymentRequest paymentRequest = PaymentRequest
                .builder()
                .amount(orderRequest.getTotalAmount())
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully. Changing the order status to PLACED");

            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occurred in payment. Changing order status to FAILED");
            orderStatus = "FAILED";
        }

        order.setOrderStatus(orderStatus);

        orderRespository.save(order);
        log.info("Order placed succesfully with orderId :: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(Long orderId) {

        log.info("Getting order completed order details with orderId :: {}", orderId);

        Order order = orderRespository.findById(orderId).orElseThrow(
                () -> new OrderServiceCustomException("Order not found with id " + orderId,
                        "NOT_FOUND", 404)
        );

        log.info("Invoking product service to fetch the product with productId :: {}", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

        OrderResponse.ProductDetails productDetails
                = OrderResponse.ProductDetails
                .builder()
                .price(productResponse.getPrice())
                .productId(productResponse.getProductId())
                .productName(productResponse.getProductName())
                .quantity(productResponse.getQuantity())
                .build();

        log.info("Invoking payment service to fetch the payment deatils for orderId :: {}", order.getId());

        PaymentResponse paymentResponse = null;

        try{
            paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(), PaymentResponse.class);
            log.info("Payment Response :: {}",paymentResponse);

            if(paymentResponse==null){
                throw new OrderServiceCustomException("Payment details not available",
                        "NOT_FOUND",404);
            }
        }catch (Exception e){
            throw new OrderServiceCustomException("Internal Server Error","INTERNAL_SERVER_ERROR",500);
        }

        OrderResponse.PaymentDetails paymentDetails
                = OrderResponse.PaymentDetails
                .builder()
                .orderId(paymentResponse.getPaymentId())
                .paymentDate(paymentResponse.getPaymentDate())
                .status(paymentResponse.getStatus())
                .paymentMode(paymentResponse.getPaymentMode())
                .paymentId(paymentResponse.getPaymentId())
                .amount(paymentResponse.getAmount())
                .build();



        OrderResponse orderResponse = OrderResponse
                .builder()
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderId(order.getId())
                .paymentDetails(paymentDetails)
                .productDetails(productDetails)
                .build();

        return orderResponse;
    }
}
