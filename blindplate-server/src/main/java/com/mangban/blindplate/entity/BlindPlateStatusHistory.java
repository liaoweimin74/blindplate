package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_status_history")
public class BlindPlateStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blindPlateId;

    @Column(length = 30)
    private String previousStatus;

    @Column(length = 30)
    private String newStatus;

    @CreationTimestamp
    private LocalDateTime changedAt;

    @Column(length = 50)
    private String operator;

    @Column(length = 500)
    private String reason;
}
