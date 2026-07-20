package com.mangban.report.controller;

import com.mangban.common.result.Result;
import com.mangban.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(reportService.getStatistics());
    }
}
