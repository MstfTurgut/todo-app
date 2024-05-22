package com.mstftrgt.todoapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeItemStatusRequest {

    private Boolean status;

    public boolean isMarked() {
        return status;
    }
}
