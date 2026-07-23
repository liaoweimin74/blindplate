package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_stocktake")
public class BlindPlateStocktake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String batchNo;

    @Column(length = 100)
    private String batchName;

    @Column(length = 50)
    private String operator;

    @Column(length = 20)
    private String status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime closedAt;
}
