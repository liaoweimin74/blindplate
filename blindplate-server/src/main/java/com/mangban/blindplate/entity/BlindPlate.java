package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false, length = 100)
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

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
