package com.mstftrgt.todoapp.controller;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.dto.request.NewListRequest;
import com.mstftrgt.todoapp.entity.UserEntity;
import com.mstftrgt.todoapp.service.ListService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("lists")
public class ListController {

    private final ListService listService;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ListDto> getAllLists() {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return listService.getAllLists(userEntity.getId());
    }

    @DeleteMapping("{listId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteList(@PathVariable String listId) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        listService.deleteList(listId, userEntity.getId());
    }

    @GetMapping("/get/{listId}")
    @ResponseStatus(code = HttpStatus.OK)
    public ListDto getListById(@PathVariable String listId) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return listService.getListById(listId, userEntity.getId());
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ListDto createList(@RequestBody @Valid NewListRequest listRequest, UriComponentsBuilder ucb, HttpServletResponse response) {
        UserEntity userEntity = (UserEntity)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ListDto listDto = listService.createList(listRequest, userEntity.getId());
        URI locationOfNewList = ucb.path("lists/get/{listId}").buildAndExpand(listDto.getId()).toUri();

        response.setHeader("Location", locationOfNewList.toString());

        return listDto;
    }
}

