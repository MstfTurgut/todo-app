package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.model.ListDto;
import com.mstftrgt.todoapp.entity.ListEntity;
import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ListServiceTests {

    @Mock
    private ListRepository listRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ListService listService;


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

        //şunu iddia et
        assertThat(result).isEqualTo(dtoList);

        Mockito.verify(listRepository).findByUserId(userId);
        Mockito.verify(modelMapper).map(listEntity, ListDto.class);
        Mockito.verify(modelMapper).map(listEntity1, ListDto.class);
        Mockito.verify(modelMapper).map(listEntity2, ListDto.class);

    }

}
