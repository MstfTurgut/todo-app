package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.dto.request.NewListRequest;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.ListAlreadyExistsException;
import com.mstftrgt.todoapp.repository.ListRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ModelMapper modelMapper;
    private final ItemService itemService;
    private final ListRepository listRepository;
    private final ListValidateService listValidateService;

    public List<ListDto> getAllLists(String userId) {
        List<ListEntity> listEntityList = listRepository.findByUserId(userId);

        return listEntityList.stream()
                .map(listEntity -> modelMapper.map(listEntity, ListDto.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteList(String listId, String userId) {
        ListEntity listEntity = listValidateService.findAndValidateById(listId, userId);

        itemService.deleteAllByListId(listId);

        listRepository.delete(listEntity);
    }

    public ListDto createList(NewListRequest listRequest, String userId) {
        validateListNotExistsByName(listRequest.getName());

        ListEntity newListEntity = ListEntity.builder()
                .name(listRequest.getName())
                .userId(userId)
                .build();

        ListEntity savedListEntity = listRepository.save(newListEntity);

        return modelMapper.map(savedListEntity, ListDto.class);
    }

    public ListDto getListById(String listId, String userId) {
        ListEntity listEntity = listValidateService.findAndValidateById(listId, userId);
        return modelMapper.map(listEntity, ListDto.class);
    }

    private void validateListNotExistsByName(String listName) {
        Optional<ListEntity> listEntityOptional = listRepository.findListByName(listName);
        if(listEntityOptional.isPresent()) {
            throw new ListAlreadyExistsException(listName);
        }
    }
}
