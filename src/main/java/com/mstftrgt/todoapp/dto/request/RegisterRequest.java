package com.mstftrgt.todoapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 8, max = 16,message = "The username length must be between 8 and 16 characters.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 16,message = "The password length must be between 8 and 16 characters.")
    private String password;
}
