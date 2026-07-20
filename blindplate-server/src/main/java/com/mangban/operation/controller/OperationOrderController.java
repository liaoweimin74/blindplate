package com.mangban.operation.controller;

import com.mangban.common.result.Result;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.service.OperationOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
public class OperationOrderController {

    private final OperationOrderService orderService;

    @GetMapping
    public Result<List<OperationOrder>> list() {
        return Result.success(orderService.findAll());
    }

    @GetMapping("/{id}")
    public Result<OperationOrder> getById(@PathVariable Long id) {
        return Result.success(orderService.findById(id));
    }

    @PostMapping
    public Result<OperationOrder> create(@RequestBody OperationOrder order) {
        return Result.success(orderService.create(order));
    }

    @PutMapping("/{id}")
    public Result<OperationOrder> update(@PathVariable Long id, @RequestBody OperationOrder order) {
        return Result.success(orderService.update(id, order));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.success(null);
    }
}
