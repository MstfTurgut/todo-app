package com.mstftrgt.todoapp.repository;


import com.mstftrgt.todoapp.entity.DependencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DependencyRepository extends JpaRepository<DependencyEntity, String> {

    List<DependencyEntity> findByItemId(String itemId);
    void deleteAllByItemIdOrDependentItemId(String itemId, String dependentItemId);
    Optional<DependencyEntity> findByItemIdAndDependentItemId(String itemId, String dependentItemId);
}
