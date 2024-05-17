package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.UserDto;
import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.request.RegisterRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.exception.UsernameAlreadyInUseException;
import com.mstftrgt.todoapp.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, ModelMapper modelMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public UserDto signup(RegisterRequest registerRequest) {

        Optional<UserEntity> userByUsername = userRepository.findByUsername(registerRequest.getUsername());

        if(userByUsername.isPresent())
            throw new UsernameAlreadyInUseException("This username is taken.");

        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        UserEntity savedUserEntity = userRepository.save(user);

        return modelMapper.map(savedUserEntity, UserDto.class);
    }

    public UserEntity authenticate(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        return userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow();
    }
}