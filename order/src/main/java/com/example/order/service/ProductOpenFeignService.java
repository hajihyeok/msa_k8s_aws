package com.example.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.order.domain.dto.ProductResponseDTO;

/*
k8s service
@FeignClient(name = "product-service", url="http://product-service")
*/
@FeignClient(name = "product-service", url="http://product-service")
// @FeignClient(name = "product-service")
public interface ProductOpenFeignService {
    
    @GetMapping("/product/{productId}")
    public ProductResponseDTO getProductById(@PathVariable Long productId, @RequestHeader("X-User-Email") String email);

}
