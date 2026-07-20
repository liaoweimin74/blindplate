package com.mangban.inspection.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_inspection_record")
public class InspectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long planId;

    private Long inspectorId;

    @Column(length = 20)
    private String result;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime inspectedAt;
}
