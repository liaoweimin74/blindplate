package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlateStocktakeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlindPlateStocktakeItemRepository extends JpaRepository<BlindPlateStocktakeItem, Long> {
    List<BlindPlateStocktakeItem> findByBatchId(Long batchId);
}
