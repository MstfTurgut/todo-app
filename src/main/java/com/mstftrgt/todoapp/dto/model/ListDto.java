package com.mstftrgt.todoapp.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListDto {

    private String id;
    private String name;
    private LocalDateTime createDate;
}
