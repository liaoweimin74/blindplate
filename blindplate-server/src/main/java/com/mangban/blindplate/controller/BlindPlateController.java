package com.mangban.blindplate.controller;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateStatusHistory;
import com.mangban.blindplate.service.BlindPlateService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/blindplates")
@RequiredArgsConstructor
public class BlindPlateController {

    private final BlindPlateService blindPlateService;

    @GetMapping
    public Result<Page<BlindPlate>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String modelType,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String lifecycleStatus,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return Result.success(blindPlateService.findAll(keyword, modelType, material, status, lifecycleStatus, pageable));
    }

    @GetMapping("/{id}")
    public Result<BlindPlate> getById(@PathVariable Long id) {
        return Result.success(blindPlateService.findById(id));
    }

    @PostMapping
    public Result<BlindPlate> create(@RequestBody BlindPlate blindPlate) {
        return Result.success(blindPlateService.create(blindPlate));
    }

    @PutMapping("/{id}")
    public Result<BlindPlate> update(@PathVariable Long id, @RequestBody BlindPlate blindPlate) {
        return Result.success(blindPlateService.update(id, blindPlate));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        blindPlateService.delete(id);
        return Result.success(null);
    }

    @GetMapping("/{id}/status-history")
    public Result<List<BlindPlateStatusHistory>> getStatusHistory(@PathVariable Long id) {
        return Result.success(blindPlateService.getStatusHistory(id));
    }

    @GetMapping("/inspection-alerts")
    public Result<List<BlindPlate>> getInspectionAlerts() {
        return Result.success(blindPlateService.getInspectionAlerts());
    }

    @PostMapping("/import")
    public Result<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        return Result.success(blindPlateService.importExcel(file));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String modelType,
            @RequestParam(required = false) String material,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String lifecycleStatus) {
        byte[] data = blindPlateService.exportExcel(keyword, modelType, material, status, lifecycleStatus);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=blindplates.xlsx");
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] data = blindPlateService.downloadTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=blindplate_template.xlsx");
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}