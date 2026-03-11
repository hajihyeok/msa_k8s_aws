package com.example.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.order.domain.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
}
