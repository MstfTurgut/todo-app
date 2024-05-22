package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.dto.request.NewListRequest;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.exception.ListAlreadyExistsException;
import com.mstftrgt.todoapp.exception.ListNotFoundException;
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
public class ListServiceTests {

    @Mock
    private ListRepository listRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<ListEntity> listCaptor;

    private ListService listService;

    @BeforeEach
    void setUp() {
        ListValidateService listValidateService = Mockito.spy(new ListValidateService(listRepository));
        listService = new ListService(modelMapper, itemService, listRepository, listValidateService);
    }

    @Test
    void shouldGetAllListEntities_whenRequested() {

        String userId = "userId";

        ListEntity listEntity = new ListEntity("1", "market", LocalDateTime.now().minusDays(30), userId);
        ListEntity listEntity1 = new ListEntity("2", "Şirket", LocalDateTime.now().minusDays(30), userId);
        ListEntity listEntity2 = new ListEntity("3", "ödev", LocalDateTime.now().minusDays(30), userId);

        ListDto listDto = new ListDto("1", "market", LocalDateTime.now().minusDays(30));
        ListDto listDto1 = new ListDto("2", "Şirket", LocalDateTime.now().minusDays(30));
        ListDto listDto2 = new ListDto("3", "ödev", LocalDateTime.now().minusDays(30));

        List<ListEntity> entityList = new ArrayList<>();
        entityList.add(listEntity);
        entityList.add(listEntity1);
        entityList.add(listEntity2);

        List<ListDto> dtoList = new ArrayList<>();
        dtoList.add(listDto);
        dtoList.add(listDto1);
        dtoList.add(listDto2);


        Mockito.when(listRepository.findByUserId(userId)).thenReturn(entityList);
        Mockito.when(modelMapper.map(listEntity, ListDto.class)).thenReturn(listDto);
        Mockito.when(modelMapper.map(listEntity1, ListDto.class)).thenReturn(listDto1);
        Mockito.when(modelMapper.map(listEntity2, ListDto.class)).thenReturn(listDto2);


        List<ListDto> result = listService.getAllLists(userId);

        assertThat(result).isEqualTo(dtoList);

        Mockito.verify(listRepository).findByUserId(userId);
        Mockito.verify(modelMapper).map(listEntity, ListDto.class);
        Mockito.verify(modelMapper).map(listEntity1, ListDto.class);
        Mockito.verify(modelMapper).map(listEntity2, ListDto.class);

    }

    @Test
    void shouldDeleteListAndItsCorrespondingItems_whenTheListIsFoundAndUserIdMatches() {
        String listId = "listId";
        String userId = "userId";
        ListEntity listEntity = new ListEntity(listId, "homework", LocalDateTime.now().minusDays(30), userId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        listService.deleteList(listId, userId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verify(itemService).deleteAllByListId(listId);
        Mockito.verify(listRepository).delete(listEntity);
    }

    @Test
    void shouldNotDeleteList_whenTheListNotFound() {
        String listId = "listId";
        String userId = "userId";

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listService.deleteList(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(itemService);
        Mockito.verify(listRepository, new Times(0)).delete(Mockito.any(ListEntity.class));
    }

    @Test
    void shouldNotDeleteList_whenListIsFoundButUserIdNotMatches() {
        String listId = "listId";
        String userId = "userId";
        String wrongUserId = "wrongUserId";
        ListEntity listEntity = new ListEntity(listId, "homework", LocalDateTime.now().minusDays(30), wrongUserId);

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> listService.deleteList(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verify(listRepository, new Times(0)).delete(Mockito.any(ListEntity.class));
    }

    @Test
    void shouldCreateNewList_whenTheListRequestIsValidAndListNotExists() {

        NewListRequest listRequest = new NewListRequest("homework");
        String userId = "userId";

        ListEntity listEntity = new ListEntity("listId", listRequest.getName(), LocalDateTime.now().minusDays(30), userId);
        ListDto listDto = new ListDto("listId", listRequest.getName(), LocalDateTime.now().minusDays(30));

        Mockito.when(listRepository.findListByName(listRequest.getName())).thenReturn(Optional.empty());
        Mockito.when(listRepository.save(Mockito.any(ListEntity.class))).thenReturn(listEntity);
        Mockito.when(modelMapper.map(listEntity, ListDto.class)).thenReturn(listDto);

        ListDto result = listService.createList(listRequest, userId);

        Mockito.verify(listRepository).findListByName(listRequest.getName());

        Mockito.verify(listRepository).save(listCaptor.capture());
        ListEntity capturedListEntity = listCaptor.getValue();
        assertThat(capturedListEntity.getName()).isEqualTo(listRequest.getName());
        assertThat(capturedListEntity.getUserId()).isEqualTo(userId);

        Mockito.verify(modelMapper).map(listEntity, ListDto.class);
    }


    @Test
    void shouldNotCreateNewList_whenTheListRequestIsValidButListAlreadyExists() {

        NewListRequest listRequest = new NewListRequest("homework");
        String userId = "userId";

        ListEntity listEntity = new ListEntity("listId", listRequest.getName(), LocalDateTime.now().minusDays(30), userId);

        Mockito.when(listRepository.findListByName(listRequest.getName())).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> listService.createList(listRequest, userId))
                .isInstanceOf(ListAlreadyExistsException.class)
                .hasMessageContaining("List already exists by name : " + listRequest.getName());

        Mockito.verify(listRepository).findListByName(listRequest.getName());
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldReturnRequestedList_whenListIsFoundAndUserIdMatches() {
        String listId = "listId";
        String userId = "userId";
        ListEntity listEntity = new ListEntity(listId, "homework", LocalDateTime.now().minusDays(30), userId);
        ListDto listDto = new ListDto(listId, "homework", LocalDateTime.now().minusDays(30));


        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));
        Mockito.when(modelMapper.map(listEntity, ListDto.class)).thenReturn(listDto);

        ListDto result = listService.getListById(listId, userId);

        assertThat(result).isEqualTo(listDto);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verify(modelMapper).map(listEntity, ListDto.class);
    }

    @Test
    void shouldNotReturnRequestedList_whenListNotFound() {
        String listId = "listId";
        String userId = "userId";

        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listService.getListById(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(modelMapper);
    }

    @Test
    void shouldNotReturnRequestedList_whenListIsFoundButUserIdDoesNotMatch() {
        String listId = "listId";
        String userId = "userId";
        String wrongUserId = "wrongUserId";
        ListEntity listEntity = new ListEntity(listId, "homework", LocalDateTime.now().minusDays(30), wrongUserId);
        ListDto listDto = new ListDto(listId, "homework", LocalDateTime.now().minusDays(30));


        Mockito.when(listRepository.findById(listId)).thenReturn(Optional.of(listEntity));

        assertThatThrownBy(() -> listService.getListById(listId, userId))
                .isInstanceOf(ListNotFoundException.class)
                .hasMessageContaining("List not found for id : " + listId);

        Mockito.verify(listRepository).findById(listId);
        Mockito.verifyNoInteractions(modelMapper);

    }


}
