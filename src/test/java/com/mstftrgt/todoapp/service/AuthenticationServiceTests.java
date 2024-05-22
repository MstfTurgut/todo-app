package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.UserDto;
import com.mstftrgt.todoapp.dto.request.LoginRequest;
import com.mstftrgt.todoapp.dto.request.RegisterRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.exception.UsernameAlreadyInUseException;
import com.mstftrgt.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void shouldRegisterNewUser_whenTheRequestIsValidAndUsernameNotTaken() {
        RegisterRequest registerRequest = new RegisterRequest("username", "password");
        String encodedPassword = "encodedPassword";
        UserEntity savedUserEntity = new UserEntity(null, "username", encodedPassword);
        UserDto expectedResult = new UserDto("userId", "username");

        Mockito.when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(encodedPassword);
        Mockito.when(userRepository.save(Mockito.any(UserEntity.class))).thenReturn(savedUserEntity);
        Mockito.when(modelMapper.map(savedUserEntity, UserDto.class)).thenReturn(expectedResult);

        UserDto result = authenticationService.signup(registerRequest);

        assertThat(expectedResult).isEqualTo(result);

        Mockito.verify(userRepository).findByUsername(registerRequest.getUsername());
        Mockito.verify(passwordEncoder).encode(registerRequest.getPassword());

        Mockito.verify(userRepository).save(userEntityCaptor.capture());
        UserEntity capturedUserEntity = userEntityCaptor.getValue();
        assertThat(capturedUserEntity.getUsername()).isEqualTo(registerRequest.getUsername());
        assertThat(capturedUserEntity.getPassword()).isEqualTo(encodedPassword);

        Mockito.verify(modelMapper).map(savedUserEntity, UserDto.class);
    }

    @Test
    void shouldNotRegisterNewUser_whenTheRequestIsValidButUsernameIsTaken() {
        RegisterRequest registerRequest = new RegisterRequest("username", "password");
        UserEntity userEntity = new UserEntity("userId", "username", "password");

        Mockito.when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.of(userEntity));

        assertThatThrownBy(() -> authenticationService.signup(registerRequest))
                .isInstanceOf(UsernameAlreadyInUseException.class)
                .hasMessageContaining("This username is taken.");

        Mockito.verify(userRepository).findByUsername(registerRequest.getUsername());
        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldLoginUser_whenTheUserFoundForTheCredentials() {

        LoginRequest loginRequest = new LoginRequest("username", "password");
        UserEntity expectedResult = new UserEntity("userId", "username", "password");

        Mockito.when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(expectedResult));

        UserEntity result = authenticationService.authenticate(loginRequest);

        assertEquals(expectedResult, result);

        Mockito.verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Mockito.verify(userRepository).findByUsername(loginRequest.getUsername());
    }

    @Test
    void shouldNotLoginUser_whenTheUserNotFoundForTheCredentials() {

        LoginRequest loginRequest = new LoginRequest("username", "password");

        Mockito.when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticate(loginRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("No value present");

        Mockito.verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        Mockito.verify(userRepository).findByUsername(loginRequest.getUsername());
    }


}