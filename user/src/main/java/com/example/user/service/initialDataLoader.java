package com.example.user.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.user.dao.UserRepository;
import com.example.user.domain.entity.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class initialDataLoader implements CommandLineRunner{
 
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>> InitialDataLoader run called.......");
        if(userRepository.findById("admin@naver.com").isPresent()) {
            return ;
        }
        
        UserEntity user = UserEntity.builder()
            .email("admin@naver.com")
            .password(passwordEncoder.encode("123456789"))
            .name("admin")
            .role("ADMIN")
            .build();
        
        userRepository.save(user);
    }
}
