package com.mangban.inspection.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bp_inspection_item")
public class InspectionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;

    @Column(length = 100)
    private String itemName;

    @Column(length = 20)
    private String result;

    @Column(length = 500)
    private String remark;
}
