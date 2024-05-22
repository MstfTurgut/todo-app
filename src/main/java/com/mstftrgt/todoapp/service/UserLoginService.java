package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.configuration.JwtTokenConfiguration;
import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.response.LoginResponse;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.jwt.JwtTokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final AuthenticationService authenticationService;
    private final JwtTokenConfiguration jwtTokenConfiguration;

    public LoginResponse login(LoginRequest loginRequest) {
        UserEntity authenticatedUser = authenticationService.authenticate(loginRequest);
        String jwtToken = jwtTokenGenerator.generateToken(authenticatedUser.getUsername());

        return LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtTokenConfiguration.getExpirationTime())
                .build();
    }
}
