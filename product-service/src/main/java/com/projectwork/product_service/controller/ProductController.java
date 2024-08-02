package com.projectwork.product_service.controller;

import com.projectwork.product_service.dto.ProductRequest;
import com.projectwork.product_service.dto.ProductResponse;
import com.projectwork.product_service.model.Product;
import com.projectwork.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductRequest productRequest) {
        productService.createProduct(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Product Successfully Created");
    }

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<ProductResponse> responses= productService.getAllProducts();
        if(responses.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Products Found In Product Service.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
