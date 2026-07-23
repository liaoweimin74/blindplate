package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_stocktake_item")
public class BlindPlateStocktakeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long batchId;

    @Column(length = 50)
    private String blindPlateCode;

    private LocalDateTime scannedAt;

    @Column(length = 30)
    private String matchStatus;
}
