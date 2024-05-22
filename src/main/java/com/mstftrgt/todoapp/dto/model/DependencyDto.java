package com.mstftrgt.todoapp.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependencyDto {

    private String id;
    private String itemId;
    private String dependentItemId;
}
