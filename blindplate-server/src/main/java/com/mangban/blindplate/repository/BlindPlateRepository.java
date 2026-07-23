package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlindPlateRepository extends JpaRepository<BlindPlate, Long>, JpaSpecificationExecutor<BlindPlate> {
    Optional<BlindPlate> findByCode(String code);
    boolean existsByCode(String code);
    List<BlindPlate> findByLifecycleStatusInOrderByNextInspectionDateAsc(List<String> statuses);
    List<BlindPlate> findByStatus(String status);

    @Query("SELECT MAX(b.qrCode) FROM BlindPlate b WHERE b.qrCode LIKE :prefix%")
    String findMaxCodeStartingWith(@Param("prefix") String prefix);
}
