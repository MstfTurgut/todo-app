package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.request.NewDependencyRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.exception.DependencyLoopException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DependencyService {

    private final DependencyRepository dependencyRepository;
    private final ItemService itemService;

    public DependencyService(DependencyRepository dependencyRepository, ItemService itemService) {
        this.dependencyRepository = dependencyRepository;
        this.itemService = itemService;
    }

    public void addDependency(NewDependencyRequest newDependencyRequest, String itemId) {

        itemService.findById(itemId);

        Optional<DependencyEntity> dependencyEntityOptional = dependencyRepository
                .findByItemIdAndDependentItemId(newDependencyRequest.getDependentItemId(), itemId);

        if(dependencyEntityOptional.isPresent())
            throw new DependencyLoopException("Cannot create dependency, this dependency causing a dependency loop.");

        DependencyEntity dependencyEntity = DependencyEntity.builder()
                .itemId(itemId)
                .dependentItemId(newDependencyRequest.getDependentItemId())
                .build();

        dependencyRepository.save(dependencyEntity);
    }
}














