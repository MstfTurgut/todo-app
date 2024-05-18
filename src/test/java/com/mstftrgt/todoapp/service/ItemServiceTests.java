package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ItemDto;
import com.mstftrgt.todoapp.dto.request.ChangeItemStatusRequest;
import com.mstftrgt.todoapp.dto.request.NewItemRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.CannotMarkItemCompletedBeforeTheDependentItemException;
import com.mstftrgt.todoapp.exception.DependentItemNotFoundException;
import com.mstftrgt.todoapp.exception.ItemNotFoundException;
import com.mstftrgt.todoapp.exception.ListNotFoundException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTests {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private DependencyRepository dependencyRepository;

    @Mock
    private ListRepository listRepository;
    @Mock
    private ModelMapper modelMapper;

    @Captor
    ArgumentCaptor<ItemEntity> itemCaptor;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        ListService listService = Mockito.spy(new ListService(listRepository, itemRepository, modelMapper));
        itemService = new ItemService(itemRepository, dependencyRepository, listService, modelMapper);
    }


    @Test
    void shouldGetAllItemsOfList_whenListIsFoundAndUserIdMatches() {

        String listId = "listId";
        String userId = "userId";

        ListEntity listEntity = new ListEntity(listId, "Market", LocalDateTime.now().minusDays(30), userId);

        ItemEntity itemEntity1 = new ItemEntity("1", "Egg", true, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(30), listId);
        ItemEntity itemEntity2 = new ItemEntity("2", "Defter", true, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(30), listId);

        List<ItemEntity> itemEntities = new ArrayList<>();

        itemEntities.add(itemEntity1);
        itemEntities.add(itemEntity2);

        ItemDto itemDto1 = new ItemDto("1", "Egg", true, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(30), listId);
        ItemDto itemDto2 = new ItemDto("2", "Defter", true, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(30), listId);

        List<ItemDto> itemDtos = new ArrayList<>();

        itemDtos.add(itemDto1);
        itemDtos.add(itemDto2);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));
        Mockito.when(itemRepository.findByListId(listId)).thenReturn(itemEntities);
        Mockito.when(modelMapper.map(itemEntity1, ItemDto.class)).thenReturn(itemDto1);
        Mockito.when(modelMapper.map(itemEntity2, ItemDto.class)).thenReturn(itemDto2);

        List<ItemDto> result = itemService.getAllItemsByListId(listId, userId);

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

        ListEntity listEntity = new ListEntity(listId, "Market", LocalDateTime.now().minusDays(30), wrongUserId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> itemService.getAllItemsByListId(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(modelMapper);

    }

    @Test
    void shouldCreateItem_whenTheRequestIsValidAndListIsFoundAndUserIdMatches() {

        String listId = "listId";
        String userId = "userId";
        NewItemRequest itemRequest = new NewItemRequest("Egg", LocalDateTime.now().plusDays(30));

        ListEntity listEntity = new ListEntity(listId, "Market", LocalDateTime.now().minusDays(30), userId);

        ItemEntity savedItem = new ItemEntity("1", itemRequest.getName(), true, itemRequest.getDeadline(), LocalDateTime.now().minusDays(30), listId);
        ItemDto itemDto = new ItemDto("1", itemRequest.getName(), true, itemRequest.getDeadline(), LocalDateTime.now().minusDays(30), listId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));
        Mockito.when(itemRepository.save(Mockito.any(ItemEntity.class))).thenReturn(savedItem);
        Mockito.when(modelMapper.map(savedItem, ItemDto.class)).thenReturn(itemDto);

        ItemDto result = itemService.createItem(itemRequest, listId, userId);

        assertThat(result).isEqualTo(itemDto);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verify(itemRepository).save(itemCaptor.capture());

        ItemEntity capturedItem = itemCaptor.getValue();

        assertThat(capturedItem.getName()).isEqualTo(itemRequest.getName());
        assertThat(capturedItem.getStatus()).isEqualTo(false);
        assertThat(capturedItem.getDeadline()).isEqualTo(itemRequest.getDeadline());
        assertThat(capturedItem.getListId()).isEqualTo(listId);

        Mockito.verify(modelMapper).map(savedItem, ItemDto.class);

    }

    @Test
    void shouldCreateItem_whenTheRequestIsValidAndListNotFound() {

        String listId = "listId";
        String userId = "userId";
        NewItemRequest itemRequest = new NewItemRequest("Egg", LocalDateTime.now().plusDays(30));


        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.createItem(itemRequest, listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(modelMapper);

    }

    @Test
    void shouldCreateItem_whenTheRequestIsValidAndListIsFoundButUserIdDoesNotMatch() {

        String listId = "listId";
        String userId = "userId";
        NewItemRequest itemRequest = new NewItemRequest("Egg", LocalDateTime.now().plusDays(30));

        String wrongUserId = "wrongUserId";

        ListEntity listEntity = new ListEntity(listId, "Market", LocalDateTime.now().minusDays(30), wrongUserId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> itemService.createItem(itemRequest, listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemRepository);
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldGetItem_whenItemFound() {

        String itemId = "itemId";

        ItemEntity itemEntity = new ItemEntity("1", "Egg", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");
        ItemDto itemDto = new ItemDto("1", "Egg", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        Mockito.when(modelMapper.map(itemEntity, ItemDto.class)).thenReturn(itemDto);

        ItemDto result = itemService.getItemById(itemId);

        assertThat(result).isEqualTo(itemDto);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(modelMapper).map(itemEntity, ItemDto.class);
    }

    @Test
    void shouldNotGetItem_whenItemNotFound() {

        String itemId = "itemId";


        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItemById(itemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found by id : " + itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verifyNoInteractions(modelMapper);

    }

    @Test
    void shouldDeleteItem_whenItemFound() {
        String itemId = "itemId";

        ItemEntity itemEntity = new ItemEntity("1", "Egg", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));

        itemService.deleteItem(itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(dependencyRepository).deleteAllByItemIdOrDependentItemId(itemId, itemId);
        Mockito.verify(itemRepository).delete(itemEntity);
    }

    @Test
    void shouldNotDeleteItem_whenItemNotFound() {

        String itemId = "itemId";

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.deleteItem(itemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found by id : " + itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verifyNoInteractions(dependencyRepository);
    }

    @Test
    void shouldChangeStatusOfAnItemToFalse_whenItemExists() {

        ChangeItemStatusRequest changeStatusRequest = new ChangeItemStatusRequest(false);
        String itemId = "itemId";

        ItemEntity itemEntity = new ItemEntity("1", "Egg", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");


        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));

        itemService.updateItemStatus(changeStatusRequest, itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(itemRepository).updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);
    }

    @Test
    void shouldChangeStatusOfAnItemToTrue_whenItemExistsAndItsPossibleDependentItemsAreMarked() {

        ChangeItemStatusRequest changeStatusRequest = new ChangeItemStatusRequest(true);
        String itemId = "itemId";
        String dependentItemId1 = "dependentItemId1";
        String dependentItemId2 = "dependentItemId2";

        ItemEntity itemEntity = new ItemEntity(itemId, "Egg", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        DependencyEntity dependencyEntity1 = new DependencyEntity("1", itemId, dependentItemId1);
        DependencyEntity dependencyEntity2 = new DependencyEntity("2", itemId, dependentItemId2);

        List<DependencyEntity> dependencyEntityList = new ArrayList<>();
        dependencyEntityList.add(dependencyEntity1);
        dependencyEntityList.add(dependencyEntity2);

        ItemEntity dependentItem1 = new ItemEntity(dependentItemId1, "Flour", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");
        ItemEntity dependentItem2 = new ItemEntity(dependentItemId2, "Milk", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        Mockito.when(dependencyRepository.findByItemId(itemId)).thenReturn(dependencyEntityList);
        Mockito.when(itemRepository.findById(dependentItemId1)).thenReturn(Optional.of(dependentItem1));
        Mockito.when(itemRepository.findById(dependentItemId2)).thenReturn(Optional.of(dependentItem2));

        itemService.updateItemStatus(changeStatusRequest, itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(dependencyRepository).findByItemId(itemId);
        Mockito.verify(itemRepository).findById(dependentItemId1);
        Mockito.verify(itemRepository).findById(dependentItemId2);
        Mockito.verify(itemRepository).updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);
    }

    @Test
    void shouldNotChangeStatusOfAnItemToTrue_whenItemExistsButOneOfItsDependentItemIsNotFound() {
        ChangeItemStatusRequest changeStatusRequest = new ChangeItemStatusRequest(true);
        String itemId = "itemId";
        String dependentItemId1 = "dependentItemId1";
        String dependentItemId2 = "dependentItemId2";

        ItemEntity itemEntity = new ItemEntity(itemId, "Egg", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        DependencyEntity dependencyEntity1 = new DependencyEntity("1", itemId, dependentItemId1);
        DependencyEntity dependencyEntity2 = new DependencyEntity("2", itemId, dependentItemId2);

        List<DependencyEntity> dependencyEntityList = new ArrayList<>();
        dependencyEntityList.add(dependencyEntity1);
        dependencyEntityList.add(dependencyEntity2);

        ItemEntity dependentItem1 = new ItemEntity(dependentItemId1, "Flour", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        Mockito.when(dependencyRepository.findByItemId(itemId)).thenReturn(dependencyEntityList);
        Mockito.when(itemRepository.findById(dependentItemId1)).thenReturn(Optional.of(dependentItem1));
        Mockito.when(itemRepository.findById(dependentItemId2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItemStatus(changeStatusRequest, itemId))
                .isInstanceOf(DependentItemNotFoundException.class)
                .hasMessageContaining("Dependent item not found.");

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(dependencyRepository).findByItemId(itemId);
        Mockito.verify(itemRepository).findById(dependentItemId1);
        Mockito.verify(itemRepository).findById(dependentItemId2);
        Mockito.verify(itemRepository, new Times(0)).updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);

    }

    @Test
    void shouldNotChangeStatusOfAnItemToTrue_whenItemExistsButOneOfItsDependentItemIsNotMarked() {

        ChangeItemStatusRequest changeStatusRequest = new ChangeItemStatusRequest(true);
        String itemId = "itemId";
        String dependentItemId1 = "dependentItemId1";
        String dependentItemId2 = "dependentItemId2";

        ItemEntity itemEntity = new ItemEntity(itemId, "Egg", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        DependencyEntity dependencyEntity1 = new DependencyEntity("1", itemId, dependentItemId1);
        DependencyEntity dependencyEntity2 = new DependencyEntity("2", itemId, dependentItemId2);

        List<DependencyEntity> dependencyEntityList = new ArrayList<>();
        dependencyEntityList.add(dependencyEntity1);
        dependencyEntityList.add(dependencyEntity2);

        ItemEntity dependentItem1 = new ItemEntity(dependentItemId1, "Flour", true, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");
        ItemEntity dependentItem2 = new ItemEntity(dependentItemId2, "Milk", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        Mockito.when(dependencyRepository.findByItemId(itemId)).thenReturn(dependencyEntityList);
        Mockito.when(itemRepository.findById(dependentItemId1)).thenReturn(Optional.of(dependentItem1));
        Mockito.when(itemRepository.findById(dependentItemId2)).thenReturn(Optional.of(dependentItem2));

        assertThatThrownBy(() -> itemService.updateItemStatus(changeStatusRequest, itemId))
                .isInstanceOf(CannotMarkItemCompletedBeforeTheDependentItemException.class)
                .hasMessageContaining("This item has dependency to other items, mark them first.");

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(dependencyRepository).findByItemId(itemId);
        Mockito.verify(itemRepository).findById(dependentItemId1);
        Mockito.verify(itemRepository).findById(dependentItemId2);
        Mockito.verify(itemRepository, new Times(0)).updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);
    }

    @Test
    void shouldNotChangeStatusOfAnItem_whenItemNotFound() {

        ChangeItemStatusRequest changeStatusRequest = new ChangeItemStatusRequest(true);
        String itemId = "itemId";


        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItemStatus(changeStatusRequest, itemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found by id : " + itemId);

        Mockito.verify(itemRepository, new Times(1)).findById(itemId);
        Mockito.verifyNoInteractions(dependencyRepository);
        Mockito.verify(itemRepository, new Times(0)).updateItemStatusByItemId(changeStatusRequest.getStatus(), itemId);

    }

}
