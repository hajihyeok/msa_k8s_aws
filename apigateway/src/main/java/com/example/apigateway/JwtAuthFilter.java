package com.example.apigateway;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component  // Spring Framework를 통해 객체 관리
public class JwtAuthFilter implements GlobalFilter{

  @Value("${jwt.secret}")
  private String secret;  // yml 파일에서 주입받은 JWT 시크릿 키

  private Key key;

  // token 검증 없이 통과하는 화이트리스트 등록
  private static final List<String> WHITE_LIST_PATHS = List.of(
    "/users/signIn",
    "/health/alive",
    "/product/list"
  );

  // 키 초기화 메서드
  @PostConstruct
  private void init() {
    System.out.println(">>>> JWT 인증 시크릿 키 : " + secret);
    // HMAC SHA 알고리즘을 사용해 키 생성
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  // 필터 메서드
  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    System.out.println(">>>> JWT 인증 필터 시작");

    // 1. Authorization 헤더 추출
    System.out.println(">>>> 1. Authorization 헤더");
    // Bearer 토큰 추출
    String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    System.out.println(">>>>>> 1) 토큰 추출(bearerToken) : " + bearerToken);

    // 엔드포인트 추출 (ex. /users/signIn)
    String endPoint = exchange.getRequest().getURI().getRawPath();
    System.out.println(">>>>>> 2) 사용자 엔드포인트 추출 : " + endPoint);

    // HTTP 메서드 추출
    String method = exchange.getRequest().getMethod().name();
    System.out.println(">>>>>> 3) 요청 메서드 : "+method); 

    // 2. 화이트리스트 경로 여부 확인
    System.out.println(">>>> 2) 화이트리스트 경로 여부 확인");
    if(WHITE_LIST_PATHS.contains(endPoint)){
      System.out.println(">>>>>> 1) 화이트리스트 경로이므로 토큰 검증 없이 통과 : " + endPoint);
      return chain.filter(exchange);
    }
    System.out.println(">>>>>> 2) 화이트리스트 경로가 아니므로 토큰 검증 수행 : " + endPoint);

    // 3. JWT 토큰 검증
    System.out.println(">>>> 3. JWT 토큰 검증");
    try {
      // 토큰이 없거나 Bearer 토큰이 아닌 경우 예외 처리
      if( bearerToken == null || !bearerToken.startsWith("Bearer ")) {
        System.out.println(">>>>>> 1) 토큰이 없거나 Bearer 토큰이 아님 : " + bearerToken);
        throw new RuntimeException("JWT 인증 필터 토큰 예외");
      }

      // 토큰 값만 추출 (Bearer 제거)
      String token = bearerToken.substring(7);
      System.out.println(">>>> JWT 인증 필터 - 토큰 : "+token); 

      // JWT 토큰 검증 및 Claims 추출
      Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
      String email = claims.getSubject(); 
            System.out.println(">>>> JWT 인증 필터 - claims get email : "+email);

            // JwtProvider 의해서 Role 입력된 경우에만 해당 
            String role = claims.get("role", String.class);
            System.out.println(">>>> JWT 인증 필터 - claims get role : "+role); 

            // X-User-Id 변수로 email 값과 Role 추가
            // X custom header 라는 것을 의미하는 관례
            ServerWebExchange modifyExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        ).build();
            return chain.filter(modifyExchange);

        } catch (Exception e) {
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
    
}
