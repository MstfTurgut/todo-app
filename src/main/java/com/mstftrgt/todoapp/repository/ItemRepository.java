package com.mstftrgt.todoapp.repository;


import com.mstftrgt.todoapp.entity.ItemEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, String> {

    List<ItemEntity> findByListId(String listId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update item set status = ?1 where id = ?2")
    void updateItemStatusByItemId(boolean status, String itemId);

    void deleteAllByListId(String listId);

}
