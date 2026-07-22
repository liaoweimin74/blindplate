package com.mangban.location.controller;

import com.mangban.common.result.Result;
import com.mangban.location.service.IsolationPointImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/isolation-points")
@RequiredArgsConstructor
public class IsolationPointImportController {

    private final IsolationPointImportService importService;

    @PostMapping("/import")
    public Result<List<String>> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        List<String> errors = importService.importFromExcel(file, userId);
        return Result.success(errors);
    }
}
