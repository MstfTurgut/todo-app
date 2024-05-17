package com.mstftrgt.todoapp.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private String id;
    private String name;
    private Boolean status;
    private LocalDateTime deadline;
    private LocalDateTime createDate;
    private String listId;
}
