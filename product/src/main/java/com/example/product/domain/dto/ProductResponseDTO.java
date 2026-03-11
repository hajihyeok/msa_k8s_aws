package com.example.product.domain.dto;

import com.example.product.domain.entity.ProductEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
        private Long id;

        private String name;

        private Integer price ;    
        
        private Integer stockQty;
    
    public static ProductResponseDTO fromEntity(ProductEntity entity) {
        return ProductResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .price(entity.getPrice())
                .stockQty(entity.getStockQty())
                .build();
    }
}
