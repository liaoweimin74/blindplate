package com.mangban.location.repository;

import com.mangban.location.entity.LocationChangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationChangeRecordRepository extends JpaRepository<LocationChangeRecord, Long> {

    List<LocationChangeRecord> findByLocationIdOrderByCreatedAtDesc(Long locationId);

    List<LocationChangeRecord> findByStatus(String status);

    List<LocationChangeRecord> findByApplicantId(Long applicantId);

    List<LocationChangeRecord> findByLocationIdAndStatus(Long locationId, String status);

    @Query("SELECT r FROM LocationChangeRecord r WHERE r.createdAt BETWEEN :start AND :end")
    List<LocationChangeRecord> findByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
