package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.request.RegisterRequest;
import com.mstftrgt.todoapp.dto.response.LoginResponse;
import com.mstftrgt.todoapp.service.AuthenticationService;
import com.mstftrgt.todoapp.service.UserLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthenticationController {

    private final UserLoginService userLoginService;
    private final AuthenticationService authenticationService;

    @PostMapping("register")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void register(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.signup(registerRequest);
    }

    @PostMapping("login")
    @ResponseStatus(code = HttpStatus.OK)
    public LoginResponse authenticate(@Valid @RequestBody LoginRequest request) {
        return userLoginService.login(request);
    }
}