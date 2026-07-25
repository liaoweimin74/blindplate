package com.mangban.blindplate.controller;

import com.mangban.blindplate.entity.BlindPlateScrapRecord;
import com.mangban.blindplate.service.ScrapService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping("/api/v1/scraps")
    public Result<BlindPlateScrapRecord> submitScrap(@RequestBody Map<String, Object> body) {
        Long blindPlateId = ((Number) body.get("blindPlateId")).longValue();
        String applicant = (String) body.get("applicant");
        String reason = (String) body.get("reason");
        return Result.success(scrapService.submitScrap(blindPlateId, applicant, reason));
    }

    @PostMapping("/api/v1/scraps/{id}/approve")
    public Result<BlindPlateScrapRecord> approveScrap(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        boolean approved = (boolean) body.get("approved");
        String approver = (String) body.get("approver");
        String comment = (String) body.getOrDefault("comment", "");
        return Result.success(scrapService.approveScrap(id, approved, approver, comment));
    }

    @GetMapping("/api/v1/scraps")
    public Result<Page<BlindPlateScrapRecord>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String applicant,
            Pageable pageable) {
        return Result.success(scrapService.findAll(status, applicant, pageable));
    }

    @GetMapping("/api/v1/scraps/by-plate/{blindPlateId}")
    public Result<List<BlindPlateScrapRecord>> getByBlindPlateId(@PathVariable Long blindPlateId) {
        return Result.success(scrapService.findByBlindPlateId(blindPlateId));
    }
}