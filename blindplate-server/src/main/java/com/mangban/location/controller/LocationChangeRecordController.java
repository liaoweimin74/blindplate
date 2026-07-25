package com.mangban.location.controller;

import com.mangban.common.result.Result;
import com.mangban.location.entity.LocationChangeRecord;
import com.mangban.location.service.LocationChangeRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/locations/change-records")
@RequiredArgsConstructor
public class LocationChangeRecordController {

    private final LocationChangeRecordService changeRecordService;

    @GetMapping
    public Result<List<LocationChangeRecord>> queryHistory(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) Long applicantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return Result.success(changeRecordService.queryHistory(locationId, status, changeType, applicantId, start, end));
    }

    @PutMapping("/{id}/approve")
    public Result<LocationChangeRecord> approveFromBody(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.TRUE.equals(body.get("approved"));
        String comment = (String) body.getOrDefault("comment", "");
        if (approved) {
            return Result.success(changeRecordService.approve(id, comment, null, true));
        } else {
            return Result.success(changeRecordService.reject(id, comment, null, true));
        }
    }

    @PostMapping("/{id}/approve")
    public Result<LocationChangeRecord> approve(@PathVariable Long id,
                                                 @RequestParam(required = false) String comment,
                                                 @RequestAttribute(value = "userId", required = false) Long userId,
                                                 @RequestAttribute(value = "isAdmin", required = false) Boolean isAdmin) {
        return Result.success(changeRecordService.approve(id, comment, userId, Boolean.TRUE.equals(isAdmin)));
    }

    @PostMapping("/{id}/reject")
    public Result<LocationChangeRecord> reject(@PathVariable Long id,
                                                @RequestParam(required = false) String reason,
                                                @RequestAttribute(value = "userId", required = false) Long userId,
                                                @RequestAttribute(value = "isAdmin", required = false) Boolean isAdmin) {
        return Result.success(changeRecordService.reject(id, reason, userId, Boolean.TRUE.equals(isAdmin)));
    }
}
