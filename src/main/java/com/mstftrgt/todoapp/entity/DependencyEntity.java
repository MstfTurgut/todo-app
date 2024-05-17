package com.mstftrgt.todoapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dependency")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DependencyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @JoinColumn(name = "item_id")
    private String itemId;

    @JoinColumn(name = "dependent_item_id")
    private String dependentItemId;
}
