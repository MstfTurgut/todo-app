package com.mstftrgt.todoapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewItemRequest {

    @NotBlank
    private String name;

    private LocalDateTime deadline;
}
