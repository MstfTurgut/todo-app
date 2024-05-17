package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.repository.ItemRepository;
import com.mstftrgt.todoapp.repository.ListRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

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


}
