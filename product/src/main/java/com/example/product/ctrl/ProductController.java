package com.example.product.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.product.domain.dto.ProductRequestDTO;
import com.example.product.domain.dto.ProductResponseDTO;
import com.example.product.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<?> create(ProductRequestDTO request, @RequestHeader("X-User-Email") String email) {
        System.out.println(">>>> product ctrl path : /create"); 
        System.out.println(">>>> params : "+ request);
        System.out.println(">>>> X-User-Email : "+ email);
        ProductResponseDTO response = productService.productCreate(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("{productId}")
    public ResponseEntity<?> getMethodName(@PathVariable Long productId, @RequestHeader("X-User-Email") String email) {
        System.out.println(">>>> product ctrl path : /productId");
        System.out.println(">>>> order-service - order service fegin connection");
        ProductResponseDTO response = productService.productRead(productId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    
}
