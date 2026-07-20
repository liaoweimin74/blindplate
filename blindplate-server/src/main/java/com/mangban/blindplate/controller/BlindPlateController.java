package com.mangban.blindplate.controller;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.service.BlindPlateService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blindplates")
@RequiredArgsConstructor
public class BlindPlateController {

    private final BlindPlateService blindPlateService;

    @GetMapping
    public Result<List<BlindPlate>> list() {
        return Result.success(blindPlateService.findAll());
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
}
