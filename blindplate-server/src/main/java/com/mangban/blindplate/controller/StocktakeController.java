package com.mangban.blindplate.controller;

import com.mangban.blindplate.entity.BlindPlateStocktake;
import com.mangban.blindplate.entity.BlindPlateStocktakeItem;
import com.mangban.blindplate.service.StocktakeService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StocktakeController {

    private final StocktakeService stocktakeService;

    @PostMapping("/api/v1/blindplates/stocktake")
    public Result<BlindPlateStocktake> createBatch(@RequestBody Map<String, String> body) {
        return Result.success(stocktakeService.createBatch(body.get("batchName"), body.get("operator")));
    }

    @PostMapping("/api/v1/blindplates/stocktake/{batchId}/scan")
    public Result<List<BlindPlateStocktakeItem>> scanCodes(@PathVariable Long batchId,
                                                            @RequestBody Map<String, List<String>> body) {
        return Result.success(stocktakeService.scanCodes(batchId, body.get("codes")));
    }

    @PutMapping("/api/v1/blindplates/stocktake/{batchId}/close")
    public Result<BlindPlateStocktake> closeBatch(@PathVariable Long batchId) {
        return Result.success(stocktakeService.closeBatch(batchId));
    }

    @GetMapping("/api/v1/blindplates/stocktake")
    public Result<Page<BlindPlateStocktake>> list(Pageable pageable) {
        return Result.success(stocktakeService.findAll(pageable));
    }

    @GetMapping("/api/v1/blindplates/stocktake/{batchId}")
    public Result<BlindPlateStocktake> getById(@PathVariable Long batchId) {
        return Result.success(stocktakeService.findById(batchId));
    }

    @GetMapping("/api/v1/blindplates/stocktake/{batchId}/items")
    public Result<List<BlindPlateStocktakeItem>> getItems(@PathVariable Long batchId) {
        return Result.success(stocktakeService.getItems(batchId));
    }
}