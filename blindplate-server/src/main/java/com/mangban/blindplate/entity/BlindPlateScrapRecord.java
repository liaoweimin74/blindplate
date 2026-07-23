package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_scrap_record")
public class BlindPlateScrapRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long blindPlateId;

    private LocalDateTime applyTime;

    @Column(length = 50)
    private String applicant;

    @Column(length = 20)
    private String status;

    @Column(length = 50)
    private String approver;

    private LocalDateTime approveTime;

    @Column(length = 500)
    private String approveComment;

    @Column(length = 500)
    private String reason;
}
