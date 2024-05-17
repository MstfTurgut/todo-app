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
    @Size(min = 8, max = 16,message = "Username length must be 8 to 16 characters long.")
    private String username;

    @NotBlank
    @Size(min = 8, max = 16,message = "Password length must be 8 to 16 characters long.")
    private String password;
}
