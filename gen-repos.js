const fs = require('fs');
const base = 'D:/aicoder/mangban/.worktrees/blind-plate-master-data/blindplate-server/src/main/java/com/mangban/blindplate/repository/';

const files = {
  'BlindPlateStatusHistoryRepository.java': `package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateStatusHistoryRepository extends JpaRepository<BlindPlateStatusHistory, Long> {
    List<BlindPlateStatusHistory> findByBlindPlateIdOrderByChangedAtDesc(Long blindPlateId);
}
`,
  'BlindPlateInspectionRepository.java': `package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateInspectionRepository extends JpaRepository<BlindPlateInspection, Long> {
    List<BlindPlateInspection> findByBlindPlateIdOrderByInspectionDateDesc(Long blindPlateId);
}
`,
  'BlindPlateScrapRecordRepository.java': `package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateScrapRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateScrapRecordRepository extends JpaRepository<BlindPlateScrapRecord, Long> {
    List<BlindPlateScrapRecord> findByBlindPlateIdOrderByApplyTimeDesc(Long blindPlateId);
    Page<BlindPlateScrapRecord> findByStatus(String status, Pageable pageable);
    boolean existsByBlindPlateIdAndStatus(Long blindPlateId, String status);
}
`,
  'BlindPlateStocktakeRepository.java': `package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStocktake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlindPlateStocktakeRepository extends JpaRepository<BlindPlateStocktake, Long> {
    Page<BlindPlateStocktake> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<BlindPlateStocktake> findByBatchNo(String batchNo);
}
`,
  'BlindPlateStocktakeItemRepository.java': `package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStocktakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateStocktakeItemRepository extends JpaRepository<BlindPlateStocktakeItem, Long> {
    List<BlindPlateStocktakeItem> findByBatchId(Long batchId);
}
`
};

for (const [name, content] of Object.entries(files)) {
  fs.writeFileSync(base + name, content);
  console.log('Created: ' + name);
}