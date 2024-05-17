package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.dto.request.NewListRequest;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.ListAlreadyExistsException;
import com.mstftrgt.todoapp.exception.ListNotFoundException;
import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListService {

    private final ListRepository listRepository;

    private final ItemRepository itemRepository;

    private final ModelMapper modelMapper;

    public ListService(ListRepository listRepository, ItemRepository itemRepository, ModelMapper modelMapper) {
        this.listRepository = listRepository;
        this.itemRepository = itemRepository;
        this.modelMapper = modelMapper;
    }

    public List<ListDto> getAllLists(String userId) {

        List<ListEntity> listEntityList = listRepository.findByUserId(userId);

        return listEntityList.stream().map(
                listEntity -> modelMapper.map(listEntity, ListDto.class))
                .collect(Collectors.toList());

    }

    public void deleteList(String listId, String userId) {

        ListEntity listEntity = findListById(listId, userId);

        itemRepository.deleteAllByListId(listId);

        listRepository.delete(listEntity);
    }

    public ListDto createList(NewListRequest listRequest, String userId) {

        Optional<ListEntity> listEntityOptional = listRepository.findListByName(listRequest.getName());

        if(listEntityOptional.isPresent()) throw new ListAlreadyExistsException("List already exists by name : " + listRequest.getName());

        ListEntity newListEntity = ListEntity.builder()
                .name(listRequest.getName())
                .userId(userId)
                .build();

        ListEntity savedListEntity = listRepository.save(newListEntity);

        return modelMapper.map(savedListEntity, ListDto.class);
    }

    public ListDto getListById(String listId, String userId) {
        ListEntity listEntity = findListById(listId, userId);
        return modelMapper.map(listEntity, ListDto.class);
    }

    protected ListEntity findListById(String listId, String userId) {
        ListEntity listEntity = listRepository.findById(listId)
                .orElseThrow(() -> new ListNotFoundException("List not found for id : " + listId));

        if (!listEntity.getUserId().equals(userId))
            throw new ListNotFoundException("List not found for id : " + listId);  // least privilege principle

        return listEntity;
    }
}
