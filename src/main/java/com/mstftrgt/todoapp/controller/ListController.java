package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.dto.request.NewListRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.ListService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("lists")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping
    public ResponseEntity<List<ListDto>> getAllLists() {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(listService.getAllLists(userEntity.getId()));
    }

    @DeleteMapping("{listId}")
    public ResponseEntity<Void> deleteList(@PathVariable String listId) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        listService.deleteList(listId, userEntity.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get/{listId}")
    public ResponseEntity<ListDto> getListById(@PathVariable String listId) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(listService.getListById(listId, userEntity.getId()));
    }

    @PostMapping
    public ResponseEntity<ListDto> createList(@RequestBody @Valid NewListRequest listRequest, UriComponentsBuilder ucb) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ListDto listDto = listService.createList(listRequest, userEntity.getId());
        URI locationOfNewList = ucb.path("lists/get/{listId}").buildAndExpand(listDto.getId()).toUri();
        return ResponseEntity.created(locationOfNewList).build();
    }
}
