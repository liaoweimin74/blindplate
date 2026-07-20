package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InspectionItemRepository extends JpaRepository<InspectionItem, Long> {
    List<InspectionItem> findByRecordId(Long recordId);
}
