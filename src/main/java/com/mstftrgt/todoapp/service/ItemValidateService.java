package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.exception.ItemNotFoundException;
import com.mstftrgt.todoapp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemValidateService {

    private final ItemRepository itemRepository;

    public ItemEntity findAndValidateById(String itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
    }
}
