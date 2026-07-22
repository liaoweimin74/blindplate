package com.mangban.blindspotstatus.controller;

import com.mangban.blindspotstatus.dto.BlindSpotStatusDTO;
import com.mangban.blindspotstatus.dto.StatusHistoryDTO;
import com.mangban.blindspotstatus.service.BlindSpotStatusService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blind-spot-status")
@RequiredArgsConstructor
public class BlindSpotStatusController {

    private final BlindSpotStatusService blindSpotStatusService;

    @GetMapping
    public Result<List<BlindSpotStatusDTO>> list(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean abnormalOnly) {
        return Result.success(blindSpotStatusService.getStatusList(locationId, status, abnormalOnly));
    }

    @GetMapping("/{locationId}/history")
    public Result<List<StatusHistoryDTO>> history(@PathVariable Long locationId) {
        return Result.success(blindSpotStatusService.getHistory(locationId));
    }
}
