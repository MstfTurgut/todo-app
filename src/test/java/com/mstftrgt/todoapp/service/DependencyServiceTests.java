package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.request.NewDependencyRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.entity.ItemEntity;
import com.mstftrgt.todoapp.exception.DependencyLoopException;
import com.mstftrgt.todoapp.exception.ItemNotFoundException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import com.mstftrgt.todoapp.repository.ItemRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class DependencyServiceTests {

    @Mock
    private DependencyRepository dependencyRepository;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ListService listService;

    @Mock
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<DependencyEntity> dependencyCaptor;

    private DependencyService dependencyService;

    @BeforeEach
    void setUp() {
        ItemService itemService = Mockito.spy(new ItemService(itemRepository, dependencyRepository, listService, modelMapper));
        dependencyService = new DependencyService(dependencyRepository, itemService);
    }


    @Test
    void shouldAddDependency_whenItemExistsAndNewDependencyIsNotCausingDependencyLoop() {

        NewDependencyRequest newDependencyRequest = new NewDependencyRequest("dependentItemId");
        String itemId = "itemId";

        ItemEntity itemEntity = new ItemEntity(itemId, "Egg", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));

        dependencyService.addDependency(newDependencyRequest, itemId);

        Mockito.verify(itemRepository).findById(itemId);

        Mockito.verify(dependencyRepository).save(dependencyCaptor.capture());
        DependencyEntity capturedDependency = dependencyCaptor.getValue();
        assertThat(capturedDependency.getItemId()).isEqualTo(itemId);
        assertThat(capturedDependency.getDependentItemId()).isEqualTo(newDependencyRequest.getDependentItemId());
    }

    @Test
    void shouldNotAddDependency_whenItemNotFound() {
        NewDependencyRequest newDependencyRequest = new NewDependencyRequest("dependentItemId");
        String itemId = "itemId";

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dependencyService.addDependency(newDependencyRequest, itemId))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessageContaining("Item not found by id : " + itemId);

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verifyNoInteractions(dependencyRepository);
    }

    @Test
    void shouldNotAddDependency_whenItemExistsButNewDependencyIsCausingDependencyLoop() {

        NewDependencyRequest newDependencyRequest = new NewDependencyRequest("dependentItemId");
        String itemId = "itemId";

        ItemEntity itemEntity = new ItemEntity(itemId, "Egg", false, LocalDateTime.now().plusDays(30), LocalDateTime.now().minusDays(30), "listId");

        DependencyEntity dependencyEntity = new DependencyEntity("1", newDependencyRequest.getDependentItemId(), itemId);

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemEntity));
        Mockito.when(dependencyRepository.findByItemIdAndDependentItemId(newDependencyRequest.getDependentItemId(), itemId)).thenReturn(Optional.of(dependencyEntity));

        assertThatThrownBy(() -> dependencyService.addDependency(newDependencyRequest, itemId))
                .isInstanceOf(DependencyLoopException.class)
                .hasMessageContaining("Cannot create dependency, this dependency causing a dependency loop.");

        Mockito.verify(itemRepository).findById(itemId);
        Mockito.verify(dependencyRepository).findByItemIdAndDependentItemId(newDependencyRequest.getDependentItemId(), itemId);
        Mockito.verify(dependencyRepository, new Times(0)).save(Mockito.any(DependencyEntity.class));
    }

}
