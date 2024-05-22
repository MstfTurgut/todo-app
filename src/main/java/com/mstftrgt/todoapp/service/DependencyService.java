package com.mstftrgt.todoapp.service;

import com.mstftrgt.todoapp.dto.request.NewDependencyRequest;
import com.mstftrgt.todoapp.entity.DependencyEntity;
import com.mstftrgt.todoapp.exception.DependencyLoopException;
import com.mstftrgt.todoapp.repository.DependencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DependencyService {

    private final ItemValidateService itemValidateService;
    private final DependencyRepository dependencyRepository;

    public List<DependencyEntity> findAllByItemId(String itemId) {
        return dependencyRepository.findAllByItemId(itemId);
    }

    public void deleteAllByItemIdOrDependentItemId(String itemId) {
        dependencyRepository.deleteAllByItemIdOrDependentItemId(itemId, itemId);
    }

    public void add(NewDependencyRequest newDependencyRequest, String itemId) {
        itemValidateService.findAndValidateById(itemId);

        Optional<DependencyEntity> dependencyEntityOptional = dependencyRepository.findByItemIdAndDependentItemId(newDependencyRequest.getDependentItemId(), itemId);

        if (dependencyEntityOptional.isPresent()) {
            throw new DependencyLoopException();
        }

        DependencyEntity dependencyEntity = DependencyEntity.builder()
                .itemId(itemId)
                .dependentItemId(newDependencyRequest.getDependentItemId())
                .build();

        dependencyRepository.save(dependencyEntity);
    }
}














