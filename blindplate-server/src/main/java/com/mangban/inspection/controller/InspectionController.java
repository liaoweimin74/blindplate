package com.mangban.inspection.controller;

import com.mangban.common.result.Result;
import com.mangban.inspection.entity.InspectionItem;
import com.mangban.inspection.entity.InspectionPlan;
import com.mangban.inspection.entity.InspectionRecord;
import com.mangban.inspection.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping
    public Result<List<InspectionPlan>> listPlans() {
        return Result.success(inspectionService.listPlans());
    }

    @PostMapping
    public Result<InspectionPlan> createPlan(@RequestBody InspectionPlan plan) {
        return Result.success(inspectionService.createPlan(plan));
    }

    @PostMapping("/{id}/execute")
    public Result<InspectionRecord> execute(
            @PathVariable Long id,
            @RequestParam Long inspectorId,
            @RequestBody List<InspectionItem> items) {
        return Result.success(inspectionService.executeInspection(id, inspectorId, items));
    }

    @GetMapping("/records")
    public Result<List<InspectionRecord>> listRecords(@RequestParam Long planId) {
        return Result.success(inspectionService.listRecords(planId));
    }
}
