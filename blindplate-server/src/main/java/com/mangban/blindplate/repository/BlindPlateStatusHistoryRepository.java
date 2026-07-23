package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateStatusHistoryRepository extends JpaRepository<BlindPlateStatusHistory, Long> {
    List<BlindPlateStatusHistory> findByBlindPlateIdOrderByChangedAtDesc(Long blindPlateId);
}
