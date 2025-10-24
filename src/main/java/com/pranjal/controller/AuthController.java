package com.pranjal.controller;

import com.pranjal.dtos.AuthenticationDTOs.AuthRequest;
import com.pranjal.dtos.AuthenticationDTOs.AuthResponse;
import com.pranjal.dtos.AuthenticationDTOs.UserRequest;
import com.pranjal.dtos.AuthenticationDTOs.UserResponse;
import com.pranjal.model.User;
import com.pranjal.service.UserService;
import com.pranjal.util.JwtUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtility jwtUtility;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request){
        try {
            Authentication authentication = authenticate(request.email(), request.password());
            log.info("Authentication: {}", authentication);
            String token = jwtUtility.generateToken(request.email());
            User user = (User) authentication.getPrincipal();
            return ResponseEntity.ok().body(
                    new AuthResponse(
                            request.email(),
                            token,
                            user.getName(),
                            user.getVirtualBalance()
                    )
            );
        } catch (Exception e){
            log.error("Error while authenticating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(request));
    }

    private Authentication authenticate(String email, String password){
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,
                password));
    }
}
