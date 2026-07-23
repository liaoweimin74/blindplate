package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_inspection")
public class BlindPlateInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blindPlateId;

    private LocalDate inspectionDate;

    @Column(length = 20)
    private String result;

    private LocalDate nextInspectionDate;

    @Column(length = 50)
    private String inspector;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
