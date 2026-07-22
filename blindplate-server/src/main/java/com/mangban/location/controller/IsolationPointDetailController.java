package com.mangban.location.controller;

import com.mangban.common.result.Result;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.service.IsolationPointDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/isolation-points")
@RequiredArgsConstructor
public class IsolationPointDetailController {

    private final IsolationPointDetailService detailService;

    @GetMapping("/{locationId}")
    public Result<IsolationPointDetail> getByLocationId(@PathVariable Long locationId) {
        return Result.success(detailService.getByLocationId(locationId));
    }

    @PutMapping("/{locationId}")
    public Result<IsolationPointDetail> update(@PathVariable Long locationId,
                                                @RequestBody IsolationPointDetail patch) {
        return Result.success(detailService.update(locationId, patch));
    }
}
