package com.projectwork.order_service.controller;

import com.projectwork.order_service.dto.OrderRequest;
import com.projectwork.order_service.model.Order;
import com.projectwork.order_service.services.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        String result = orderService.placeOrder(orderRequest);
        if(result.equals("Order Placed"))
        {
            return ResponseEntity.status(HttpStatus.OK).body("Order Placed Successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Method Return type should be same as @CircuitBreaker Method in this case public ResponseEntity<String> placeOrder
    public ResponseEntity<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Oops! Something went wrong, please order After some time!");
    }
}
/*
 Without the TimeLimiter
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        String result = orderService.placeOrder(orderRequest);
        if(result.equals("Order Placed"))
        {
            return ResponseEntity.status(HttpStatus.OK).body("Order Placed Successfully");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }



    fallback looks like this without timeout
    // Method Return type should be same as @CircuitBreaker Method in this case public ResponseEntity<String> placeOrder
    public ResponseEntity<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Oops! Something went wrong, please order After some time!");
    }
 */

/*
 using the TimeLimiter
@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));

     // Method Return type should be same as @CircuitBreaker Method in this case public ResponseEntity<String> placeOrder
    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(() -> "Oops! Something went wrong, please order After some time!");
    }
    }*/