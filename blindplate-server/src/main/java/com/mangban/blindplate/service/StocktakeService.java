package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateStocktake;
import com.mangban.blindplate.entity.BlindPlateStocktakeItem;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.repository.BlindPlateStocktakeItemRepository;
import com.mangban.blindplate.repository.BlindPlateStocktakeRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StocktakeService {

    private final BlindPlateStocktakeRepository stocktakeRepository;
    private final BlindPlateStocktakeItemRepository stocktakeItemRepository;
    private final BlindPlateRepository blindPlateRepository;

    private String generateBatchNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxBatchNo = null;
        Optional<BlindPlateStocktake> latest = stocktakeRepository.findByBatchNo("ST-" + datePart + "-0001");
        // Simple approach: find by batch prefix
        List<BlindPlateStocktake> today = stocktakeRepository.findAll().stream()
                .filter(s -> s.getBatchNo() != null && s.getBatchNo().startsWith("ST-" + datePart))
                .toList();
        int seq = 1;
        for (BlindPlateStocktake s : today) {
            String num = s.getBatchNo().substring(s.getBatchNo().lastIndexOf('-') + 1);
            try {
                int n = Integer.parseInt(num);
                if (n >= seq) seq = n + 1;
            } catch (NumberFormatException ignored) {}
        }
        return "ST-" + datePart + "-" + String.format("%04d", seq);
    }

    public BlindPlateStocktake createBatch(String batchName, String operator) {
        BlindPlateStocktake batch = new BlindPlateStocktake();
        batch.setBatchNo(generateBatchNo());
        batch.setBatchName(batchName);
        batch.setOperator(operator);
        batch.setStatus("in_progress");
        return stocktakeRepository.save(batch);
    }

    public List<BlindPlateStocktakeItem> scanCodes(Long batchId, List<String> codes) {
        BlindPlateStocktake batch = stocktakeRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(404, "盘点批次不存在"));
        if ("closed".equals(batch.getStatus())) {
            throw new BusinessException(400, "盘点批次已关闭，无法扫描");
        }
        LocalDateTime now = LocalDateTime.now();
        List<BlindPlateStocktakeItem> items = codes.stream().map(code -> {
            BlindPlateStocktakeItem item = new BlindPlateStocktakeItem();
            item.setBatchId(batchId);
            item.setBlindPlateCode(code);
            item.setScannedAt(now);
            item.setMatchStatus("pending");
            return item;
        }).toList();
        return stocktakeItemRepository.saveAll(items);
    }

    public BlindPlateStocktake closeBatch(Long batchId) {
        BlindPlateStocktake batch = stocktakeRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException(404, "盘点批次不存在"));
        if ("closed".equals(batch.getStatus())) {
            throw new BusinessException(400, "盘点批次已关闭");
        }

        List<BlindPlate> inStock = blindPlateRepository.findByStatus("in_stock");
        Set<String> inStockCodes = inStock.stream().map(BlindPlate::getCode).collect(Collectors.toSet());
        List<BlindPlateStocktakeItem> existingItems = stocktakeItemRepository.findByBatchId(batchId);
        Set<String> scannedCodes = existingItems.stream().map(BlindPlateStocktakeItem::getBlindPlateCode).collect(Collectors.toSet());

        // Update existing scanned items
        for (BlindPlateStocktakeItem item : existingItems) {
            Optional<BlindPlate> plate = blindPlateRepository.findByCode(item.getBlindPlateCode());
            if (plate.isPresent() && "in_stock".equals(plate.get().getStatus())) {
                item.setMatchStatus("matched");
            } else if (plate.isPresent()) {
                item.setMatchStatus("location_mismatch");
            } else {
                item.setMatchStatus("unexpected");
            }
        }
        stocktakeItemRepository.saveAll(existingItems);

        // Create items for missing plates
        for (String code : inStockCodes) {
            if (!scannedCodes.contains(code)) {
                BlindPlateStocktakeItem item = new BlindPlateStocktakeItem();
                item.setBatchId(batchId);
                item.setBlindPlateCode(code);
                item.setMatchStatus("missing");
                stocktakeItemRepository.save(item);
            }
        }

        batch.setStatus("closed");
        batch.setClosedAt(LocalDateTime.now());
        return stocktakeRepository.save(batch);
    }

    public Page<BlindPlateStocktake> findAll(Pageable pageable) {
        return stocktakeRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public BlindPlateStocktake findById(Long id) {
        return stocktakeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "盘点批次不存在"));
    }

    public List<BlindPlateStocktakeItem> getItems(Long batchId) {
        return stocktakeItemRepository.findByBatchId(batchId);
    }
}