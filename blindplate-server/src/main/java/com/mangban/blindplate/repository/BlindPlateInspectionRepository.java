package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateInspectionRepository extends JpaRepository<BlindPlateInspection, Long> {
    List<BlindPlateInspection> findByBlindPlateIdOrderByInspectionDateDesc(Long blindPlateId);
}
