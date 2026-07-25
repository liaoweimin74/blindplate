package com.mangban.location.controller;

import com.mangban.common.result.Result;
import com.mangban.location.entity.Location;
import com.mangban.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public Result<List<Location>> list() {
        return Result.success(locationService.findAll());
    }

    @GetMapping("/tree")
    public Result<List<Location>> getTree() {
        return Result.success(locationService.getTree());
    }

    @GetMapping("/{id}")
    public Result<Location> getById(@PathVariable Long id) {
        return Result.success(locationService.findById(id));
    }

    @PostMapping
    public Result<Location> create(@RequestBody Location location) {
        return Result.success(locationService.create(location));
    }

    @PutMapping("/{id}")
    public Result<Location> update(@PathVariable Long id, @RequestBody Location location) {
        return Result.success(locationService.update(id, location));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return Result.success(null);
    }
}
