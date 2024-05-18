package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.dto.request.ChangeItemStatusRequest;
import com.mstftrgt.todoapp.dto.request.NewItemRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.exception.CannotMarkItemCompletedBeforeTheDependentItemException;
import com.mstftrgt.todoapp.exception.DependentItemNotFoundException;
import com.mstftrgt.todoapp.exception.ItemNotFoundException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import com.mstftrgt.todoapp.repository.ItemRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    private final DependencyRepository dependencyRepository;

    private final ListService listService;

    private final ModelMapper modelMapper;

    public ItemService(ItemRepository itemRepository, DependencyRepository dependencyRepository, ListService listService, ModelMapper modelMapper) {
        this.itemRepository = itemRepository;
        this.dependencyRepository = dependencyRepository;
        this.listService = listService;
        this.modelMapper = modelMapper;
    }

    public List<ItemDto> getAllItemsByListId(String listId, String userId) {

        listService.findListById(listId, userId);

        List<ItemEntity> itemEntityList = itemRepository.findByListId(listId);

        return itemEntityList.stream()
                .map(itemEntity -> modelMapper.map(itemEntity, ItemDto.class))
                .collect(Collectors.toList());

    }

    public ItemDto createItem(NewItemRequest itemRequest, String listId, String userId) {

        listService.findListById(listId, userId);

        ItemEntity itemEntity = ItemEntity.builder()
                .name(itemRequest.getName())
                .status(false)
                .deadline(itemRequest.getDeadline())
                .listId(listId)
                .build();

        ItemEntity savedItemEntity = itemRepository.save(itemEntity);

        return modelMapper.map(savedItemEntity, ItemDto.class);
    }

    public ItemDto getItemById(String itemId) {

        ItemEntity itemEntity = findById(itemId);
        return modelMapper.map(itemEntity, ItemDto.class);
    }

    public void deleteItem(String itemId) {

        ItemEntity itemEntity = findById(itemId);

        dependencyRepository.deleteAllByItemIdOrDependentItemId(itemId, itemId);

        itemRepository.delete(itemEntity);
    }

    public void updateItemStatus(ChangeItemStatusRequest changeStatusRequest, String itemId) {

        findById(itemId);

        if(changeStatusRequest.getStatus() == true) {

            List<DependencyEntity> dependencyEntityList = dependencyRepository.findByItemId(itemId);

            dependencyEntityList
                    .forEach(dependencyEntity -> {

                        Optional<ItemEntity> dependentItemOptional =
                                itemRepository.findById(dependencyEntity.getDependentItemId());

                        if(dependentItemOptional.isEmpty())
                            throw new DependentItemNotFoundException("Dependent item not found.");

                        if(dependentItemOptional.get().getStatus() == false)
                            throw new CannotMarkItemCompletedBeforeTheDependentItemException("This item has dependency to other items, mark them first.");
                    });

        }

        itemRepository.updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);
    }

    protected ItemEntity findById(String itemId) {

        Optional<ItemEntity> itemEntity = itemRepository.findById(itemId);
        if(itemEntity.isEmpty()) throw new ItemNotFoundException("Item not found by id : " + itemId);
        return itemEntity.get();
    }
}














