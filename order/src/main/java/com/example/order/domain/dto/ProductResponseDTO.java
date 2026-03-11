package com.example.order.domain.dto;

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
}
