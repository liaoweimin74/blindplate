package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_blind_plate")
public class BlindPlate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(length = 100)
    private String name;

    @Column(length = 50)
    private String spec;

    @Column(length = 50)
    private String material;

    private Integer diameter;

    private Double pressure;

    @Column(length = 100)
    private String manufacturer;

    @Column(length = 20)
    private String status;

    @Column(length = 500)
    private String remark;

    @Column(length = 50)
    private String modelType;

    private Double thickness;

    @Column(length = 100)
    private String factoryCode;

    private LocalDate purchaseDate;

    private Long currentLocationId;

    private Integer installCount = 0;

    private Double totalUsageDays = 0.0;

    @Column(length = 30)
    private String lifecycleStatus = "normal";

    private LocalDate nextInspectionDate;

    @Column(length = 100)
    private String rfidTag;

    @Column(length = 100)
    private String qrCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
