package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.model.UserDto;
import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.request.RegisterRequest;
import com.mstftrgt.todoapp.dto.response.LoginResponse;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.AuthenticationService;
import com.mstftrgt.todoapp.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto registeredUser = authenticationService.signup(registerRequest);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {

        UserEntity authenticatedUser = authenticationService.authenticate(loginRequest);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = LoginResponse.builder()
                .token(jwtToken)
                .expiresIn(jwtService.getExpirationTime())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}