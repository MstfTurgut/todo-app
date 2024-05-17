package com.mstftrgt.todoapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "list")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @JoinColumn(name = "user_id")
    private String userId;

}
