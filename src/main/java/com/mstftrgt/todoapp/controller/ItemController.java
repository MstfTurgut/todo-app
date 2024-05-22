package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.dto.request.ChangeItemStatusRequest;
import com.mstftrgt.todoapp.dto.request.NewItemRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.ItemService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("{listId}")
    public List<ItemDto> getAllItemsByListId(@PathVariable String listId) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return itemService.getAllByListId(listId, userEntity.getId());
    }

    @GetMapping("/get/{itemId}")
    public ItemDto getItemById(@PathVariable String itemId) {
        return itemService.getById(itemId);
    }

    @PostMapping("{listId}")
    public ItemDto createItem(@Valid @RequestBody NewItemRequest itemRequest, @PathVariable String listId, UriComponentsBuilder ucb, HttpServletResponse response) {
        UserEntity userEntity = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ItemDto itemDto = itemService.create(itemRequest, listId, userEntity.getId());

        URI locationOfNewList = ucb.path("lists/get/{listId}").buildAndExpand(itemDto.getId()).toUri();
        response.setHeader("Location", locationOfNewList.toString());

        return itemDto;
    }

    @DeleteMapping("{itemId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable String itemId) {
        itemService.deleteById(itemId);
    }

    @PatchMapping("{itemId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateItemStatus(@PathVariable String itemId, @Valid @RequestBody ChangeItemStatusRequest changeStatusRequest) {
        itemService.updateStatus(changeStatusRequest, itemId);
    }
}
