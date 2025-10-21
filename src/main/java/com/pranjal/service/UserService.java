package com.pranjal.service;

import com.pranjal.dtos.AuthenticationDTOs.UserRequest;
import com.pranjal.dtos.AuthenticationDTOs.UserResponse;
import com.pranjal.exception.UserAlreadyExistException;
import com.pranjal.exception.UserNotFoundException;
import com.pranjal.model.Holding;
import com.pranjal.model.User;
import com.pranjal.repository.HoldingRepository;
import com.pranjal.repository.UserRepository;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HoldingRepository holdingRepository;

    @Transactional
    public UserResponse save(UserRequest request) {
        if(userRepository.existsByEmail(request.email())){
            throw new UserAlreadyExistException("User with email: " + request.email() + " already" +
                    " exist.");
        }
        User user = userRepository.save(
                User.builder()
                        .userId(UUID.randomUUID().toString())
                        .email(request.email())
                        .name(request.name())
                        .virtualBalance(100000.00)
                        .password(passwordEncoder.encode(request.password()))
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getVirtualBalance(),
                user.getCreatedAt().toString()
        );
    }

    @Transactional(readOnly = true)
    public String getUserIdByEmail(String email){
        User user =  userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }

    public String getAllSymbol(String userId){
        List<Holding> holdings = holdingRepository.findAllByUser_UserIdOrderByStockSymbolAsc(userId);
        if(holdings.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < holdings.size() - 1; i++){
            sb.append(holdings.get(i).getStockSymbol()).append(",");
        }
        sb.append(holdings.getLast().getStockSymbol());
        return sb.toString();
    }
}
