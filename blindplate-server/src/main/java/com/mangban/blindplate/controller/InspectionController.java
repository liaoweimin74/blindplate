package com.mangban.blindplate.controller;

import com.mangban.blindplate.entity.BlindPlateInspection;
import com.mangban.blindplate.service.InspectionService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("blindPlateInspectionController")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @PostMapping("/api/v1/blindplates/{blindPlateId}/inspections")
    public Result<BlindPlateInspection> create(@PathVariable Long blindPlateId,
                                               @RequestBody BlindPlateInspection inspection) {
        return Result.success(inspectionService.create(blindPlateId, inspection));
    }

    @GetMapping("/api/v1/blindplates/{blindPlateId}/inspections")
    public Result<List<BlindPlateInspection>> list(@PathVariable Long blindPlateId) {
        return Result.success(inspectionService.findByBlindPlateId(blindPlateId));
    }

    @PutMapping("/api/v1/inspections/{id}")
    public Result<BlindPlateInspection> update(@PathVariable Long id,
                                               @RequestBody BlindPlateInspection inspection) {
        return Result.success(inspectionService.update(id, inspection));
    }

    @DeleteMapping("/api/v1/inspections/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        inspectionService.delete(id);
        return Result.success(null);
    }
}