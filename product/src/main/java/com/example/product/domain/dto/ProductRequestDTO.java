package com.example.product.domain.dto;

import com.example.product.domain.entity.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    private String name;
    
    private Integer price;
    
    private Integer stockQty;

        public ProductEntity toEntity(String email) {
        return ProductEntity.builder()
                .name(this.name)
                .price(this.price)
                .stockQty(this.stockQty)
                .email(email)
                .build();
    }
}