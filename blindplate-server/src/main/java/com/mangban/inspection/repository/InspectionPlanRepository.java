package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionPlanRepository extends JpaRepository<InspectionPlan, Long> {
}
