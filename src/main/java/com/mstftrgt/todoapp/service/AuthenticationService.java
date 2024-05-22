package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.UserDto;
import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.request.RegisterRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.exception.UsernameAlreadyInUseException;
import com.mstftrgt.todoapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserDto signup(RegisterRequest registerRequest) {
        Optional<UserEntity> userByUsername = userRepository.findByUsername(registerRequest.getUsername());
        if (userByUsername.isPresent()) {
            throw new UsernameAlreadyInUseException();
        }

        UserEntity user = UserEntity.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build();

        UserEntity savedUserEntity = userRepository.save(user);

        return modelMapper.map(savedUserEntity, UserDto.class);
    }

    public UserEntity authenticate(LoginRequest request) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(authentication);

        return userRepository.findByUsername(request.getUsername())
                .orElseThrow();
    }
}