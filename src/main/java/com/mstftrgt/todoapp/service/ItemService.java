package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.dto.request.ChangeItemStatusRequest;
import com.mstftrgt.todoapp.dto.request.NewItemRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.exception.CannotMarkItemCompletedBeforeTheDependentItemException;
import com.mstftrgt.todoapp.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final DependencyService dependencyService;
    private final ItemValidateService itemValidateService;
    private final ListValidateService listValidateService;

    public List<ItemDto> getAllByListId(String listId, String userId) {
        listValidateService.findAndValidateById(listId, userId);
        List<ItemEntity> itemEntityList = itemRepository.findByListId(listId);

        return itemEntityList.stream()
                .map(itemEntity -> modelMapper.map(itemEntity, ItemDto.class))
                .collect(Collectors.toList());
    }

    public ItemDto create(NewItemRequest itemRequest, String listId, String userId) {
        listValidateService.findAndValidateById(listId, userId);
        ItemEntity itemEntity = ItemEntity.builder()
                .name(itemRequest.getName())
                .status(false)
                .deadline(itemRequest.getDeadline())
                .listId(listId)
                .build();

        ItemEntity savedItemEntity = itemRepository.save(itemEntity);

        return modelMapper.map(savedItemEntity, ItemDto.class);
    }

    public ItemDto getById(String itemId) {
        ItemEntity itemEntity = itemValidateService.findAndValidateById(itemId);
        return modelMapper.map(itemEntity, ItemDto.class);
    }

    @Transactional
    public void deleteById(String itemId) {
        ItemEntity itemEntity = itemValidateService.findAndValidateById(itemId);
        dependencyService.deleteAllByItemIdOrDependentItemId(itemId);
        itemRepository.delete(itemEntity);
    }

    public void deleteAllByListId(String listId) {
        itemRepository.deleteAllByListId(listId);
    }

    public void updateStatus(ChangeItemStatusRequest changeStatusRequest, String itemId) {
        itemValidateService.findAndValidateById(itemId);

        if (changeStatusRequest.isMarked()) {
            validateAllDependenciesAreMarked(itemId);
        }

        itemRepository.updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);
    }

    private void validateAllDependenciesAreMarked(String itemId) {
        dependencyService.findAllByItemId(itemId).forEach(this::validateItemIsMarked);
    }

    private void validateItemIsMarked(DependencyEntity dependencyEntity) {
        ItemEntity item = itemValidateService.findAndValidateById(dependencyEntity.getDependentItemId());
        if (item.isNotMarked()) {
            throw new CannotMarkItemCompletedBeforeTheDependentItemException();
        }
    }

}














