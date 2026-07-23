package com.mangban.blindplate.repository;

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
