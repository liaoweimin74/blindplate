package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlindPlateRepository extends JpaRepository<BlindPlate, Long> {
    Optional<BlindPlate> findByCode(String code);
    boolean existsByCode(String code);
}
