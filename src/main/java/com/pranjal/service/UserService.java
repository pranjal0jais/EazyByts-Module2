package com.pranjal.service;

import com.pranjal.dtos.AuthenticationDTOs.UserRequest;
import com.pranjal.dtos.AuthenticationDTOs.UserResponse;
import com.pranjal.model.User;
import com.pranjal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse save(UserRequest request) {
        User user = userRepository.save(
                User.builder()
                        .userId(UUID.randomUUID().toString())
                        .email(request.email())
                        .name(request.name())
                        .virtualBalance(100000.00)
                        .password(passwordEncoder.encode(request.password()))
                        .build()
        );
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getVirtualBalance()
        );
    }
}
