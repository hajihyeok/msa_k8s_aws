package com.example.user.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user.dao.UserRepository;
import com.example.user.domain.dto.UserRequestDTO;
import com.example.user.domain.dto.UserResponseDTO;
import com.example.user.domain.entity.UserEntity;
import com.example.user.provider.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtProvider jwtProvider;

    // redis
    @Qualifier("tokenRedis")
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_TTL = 60 * 60 * 24 * 7; // 7일

    public Map<String, Object> signIn(UserRequestDTO request) {
        System.out.println(">>>> user service signin");
        Map<String, Object> map = new HashMap<>();

        System.out.println(">>>> 1. user service 사용자 조회");

        // 사용자 존재 여부 조회
        // hashing version
        UserEntity entity = userRepository.findById(request.getEmail())
                                .orElseThrow(() -> 
                                    new RuntimeException("Not Found!!")) ;  

        // 패스워드 일치 여부 조회
        // (plain vs encoded) 
        if( !passwordEncoder.matches(request.getPassword(), entity.getPassword())) {
            throw new RuntimeException("Password Not Matched");
        }


        System.out.println(">>>> 2. user service 토큰 생성"); 
        String at = jwtProvider.createAT(entity.getEmail());
        String rt = jwtProvider.createRT(entity.getEmail());
        
        // 참조 관계를 최대한 없애기 위해 함수 호출 형식이 아니라 직접 코드 기입
        System.out.println(">>>> 3. user service RT토큰 Redis 저장"); 
        redisTemplate.opsForValue()
            .set("RT:"+entity.getEmail(), rt, REFRESH_TOKEN_TTL, TimeUnit.SECONDS); 

                          
        map.put("response", UserResponseDTO.fromEntity(entity));
        map.put("access", at);
        map.put("refresh", rt); 

        return map ;
    }
}
