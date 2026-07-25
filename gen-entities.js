const fs = require('fs');
const base = 'D:/aicoder/mangban/.worktrees/blind-plate-master-data/blindplate-server/src/main/java/com/mangban/blindplate/entity/';

const files = {
  'BlindPlateStatusHistory.java': `package com.mangban.blindplate.entity;

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
`,
  'BlindPlateInspection.java': `package com.mangban.blindplate.entity;

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
`,
  'BlindPlateScrapRecord.java': `package com.mangban.blindplate.entity;

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
`,
  'BlindPlateStocktake.java': `package com.mangban.blindplate.entity;

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
`,
  'BlindPlateStocktakeItem.java': `package com.mangban.blindplate.entity;

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
`
};

for (const [name, content] of Object.entries(files)) {
  fs.writeFileSync(base + name, content);
  console.log('Created: ' + name);
}