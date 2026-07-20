package com.mangban.operation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_operation_order")
public class OperationOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(nullable = false, length = 20)
    private String type;

    private Long blindplateId;

    private Long locationId;

    @Column(length = 20)
    private String status;

    private LocalDateTime plannedDate;

    private LocalDateTime actualDate;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
