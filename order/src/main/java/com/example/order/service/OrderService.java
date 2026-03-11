package com.example.order.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.order.dao.OrderRepository;
import com.example.order.domain.dto.OrderRequestDTO;
import com.example.order.domain.dto.OrderResponseDTO;
import com.example.order.domain.dto.ProductResponseDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ProductOpenFeignService productOpenFeignService;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    /*
    1. Client(path : /order/create) -> 주문 요청
    2. orderFeignKafkaCreate() 실행
    3. Feign 상품 조회 (동기 방식)
    4. 재고 여부 확인
    5. kafka 토픽(KafkaTemplate.send())을 발행
    6. kafka broker(messageQ)에 메시지 저장
    7. proudct-service에서 수신(KafkaListener)하여 stockConsumer() 호출
    8. 재고 감소 구현(비동기 방식)
    */
    // 하나의 함수에서 동기 통신을 여러번 할 경우 아래 코드 고려
    // @CircuitBreaker(name = "productService", fallbackMethod = "fallbackOrderFeignKafkaCreate")
    @CircuitBreaker(name = "productService", fallbackMethod = "fallbackProductService")
    public OrderResponseDTO orderFeignKafkaCreate(OrderRequestDTO request, String email) {
        System.out.println(">>>> order service orderFeignKafkaCreate");
        System.out.println(">>>> 재고 유무 판단을 위해서 product-service Feign 통신");

        ProductResponseDTO response = productOpenFeignService.getProductById(request.getProductId(), email);
        System.out.println(">>>> Feign 통신 결과 : " + response.getStockQty());

        if (response.getStockQty() < request.getQty()) {
            System.out.println(">>>> 재고 부족");
            throw new RuntimeException("재고 부족");
        } else {
            System.out.println(">>>> 재고 충분, Kafka 토픽 발행");
            kafkaTemplate.send("update-stock-topic", request);
            // 주문 저장
            return OrderResponseDTO.fromEntity(orderRepository.save(request.toEntity(email)));
        }
    }

    public OrderResponseDTO fallbackProductService(OrderRequestDTO request, String email, Throwable t) {
        throw new RuntimeException("서비스 지연으로 에러 발생됨...다시 시도해주세요.");
    }

}