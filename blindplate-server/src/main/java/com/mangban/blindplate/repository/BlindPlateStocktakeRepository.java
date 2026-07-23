package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStocktake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlindPlateStocktakeRepository extends JpaRepository<BlindPlateStocktake, Long> {
    Page<BlindPlateStocktake> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<BlindPlateStocktake> findByBatchNo(String batchNo);
}
