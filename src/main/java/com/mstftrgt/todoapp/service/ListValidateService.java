package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.ListNotFoundException;
import com.mstftrgt.todoapp.repository.ListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListValidateService {

    private final ListRepository listRepository;

    public ListEntity findAndValidateById(String listId, String userId) {
        ListEntity listEntity = listRepository.findById(listId).orElseThrow(() -> new ListNotFoundException(listId));

        if (!listEntity.getUserId().equals(userId)) {
            throw new ListNotFoundException(listId);  // least privilege principle
        }
        return listEntity;
    }
}
