package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.ListNotFoundException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private  ItemRepository itemRepository;

    @Mock
    private  DependencyRepository dependencyRepository;

    @Mock
    private ListRepository listRepository;

    @Mock
    private  ModelMapper modelMapper;

    private  ListService listService;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        listService = Mockito.spy(new ListService(listRepository, itemRepository, modelMapper));
        itemService = new ItemService(itemRepository, dependencyRepository, listService, modelMapper);
    }


    @Test
    void shouldGetAllItemsOfList_whenListIsFoundAndUserIdMatches() {

        String listId = "listId";
        String userId = "userId";

        ListEntity listEntity = new ListEntity(listId,"Market", LocalDateTime.now().minusDays(30),userId);

        ItemEntity itemEntity1 = new ItemEntity("1","Yumurta",true,LocalDateTime.now().minusDays(20),LocalDateTime.now().minusDays(30),listId);
        ItemEntity itemEntity2 = new ItemEntity("2","Defter",true,LocalDateTime.now().minusDays(20),LocalDateTime.now().minusDays(30),listId);

        List<ItemEntity> itemEntities = new ArrayList<>();

        itemEntities.add(itemEntity1);
        itemEntities.add(itemEntity2);

        ItemDto itemDto1 = new ItemDto("1","Yumurta",true,LocalDateTime.now().minusDays(20),LocalDateTime.now().minusDays(30),listId);
        ItemDto itemDto2 = new ItemDto("2","Defter",true,LocalDateTime.now().minusDays(20),LocalDateTime.now().minusDays(30),listId);

        List<ItemDto> itemDtos = new ArrayList<>();

        itemDtos.add(itemDto1);
        itemDtos.add(itemDto2);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));
        Mockito.when(itemRepository.findByListId(listId)).thenReturn(itemEntities);
        Mockito.when(modelMapper.map(itemEntity1, ItemDto.class)).thenReturn(itemDto1);
        Mockito.when(modelMapper.map(itemEntity2, ItemDto.class)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.getAllItemsByListId(listId,userId);

        assertThat(result).isEqualTo(itemDtos);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verify(itemRepository).findByListId(listId);
        Mockito.verify(modelMapper).map(itemEntity1, ItemDto.class);
        Mockito.verify(modelMapper).map(itemEntity2, ItemDto.class);
    }

    @Test
    void shouldNotGetAllItemsOfList_whenListIsNotFound() {

        String listId = "listId";
        String userId = "userId";

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getAllItemsByListId(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(modelMapper);



    }

    @Test
    void shouldNotGetAllItemsOfList_whenListIsFoundButUserIdDoesNotMatch() {

        String listId = "listId";
        String userId = "userId";

        String wrongUserId = "wrongUserId";

        ListEntity listEntity = new ListEntity(listId,"Market", LocalDateTime.now().minusDays(30),wrongUserId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> itemService.getAllItemsByListId(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(modelMapper);




    }


}
