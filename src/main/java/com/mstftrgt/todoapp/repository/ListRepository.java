package com.mstftrgt.todoapp.repository;

import com.mstftrgt.todoapp.entity.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListRepository extends JpaRepository<ListEntity, String> {

    List<ListEntity> findByUserId(String userId);

    Optional<ListEntity> findListByName(String name);
}
