package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {
    List<InspectionRecord> findByPlanId(Long planId);
}
