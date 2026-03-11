package com.example.order.domain.dto;

import com.example.order.domain.entity.OrderEntity;

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
public class OrderRequestDTO {

    private Long productId;

    private Integer qty;

    public OrderEntity toEntity(String email) {
        return OrderEntity.builder()
                .email(email)
                .productId(productId)
                .qty(qty)
                .build();
    }

}
