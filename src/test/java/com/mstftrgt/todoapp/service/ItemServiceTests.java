package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.repository.DependencyRepository;
import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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




}
