package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.dto.request.ChangeItemStatusRequest;
import com.mstftrgt.todoapp.dto.request.NewItemRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("{listId}")
    public ResponseEntity<List<ItemDto>> getAllItemsByListId(@PathVariable String listId) {

        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(itemService.getAllItemsByListId(listId, userEntity.getId()));
    }

    @GetMapping("/get/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable String itemId) {
        return ResponseEntity.ok(itemService.getItemById(itemId));
    }

    @PostMapping("{listId}")
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody NewItemRequest itemRequest,
                                              @PathVariable String listId, UriComponentsBuilder ucb) {

        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ItemDto itemDto = itemService.createItem(itemRequest, listId, userEntity.getId());
        URI locationOfNewItem = ucb.path("items/get/{itemId}").buildAndExpand(itemDto.getId()).toUri();
        return ResponseEntity.created(locationOfNewItem).build();
    }

    @DeleteMapping("{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable String itemId) {

        itemService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Void> updateItemStatus(@Valid @RequestBody ChangeItemStatusRequest changeStatusRequest,
                                           @PathVariable String itemId) {

        itemService.updateItemStatus(changeStatusRequest, itemId);
        return ResponseEntity.noContent().build();
    }

}
