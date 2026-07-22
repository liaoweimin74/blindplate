package com.mangban.blindboard.controller;

import com.mangban.blindboard.entity.BoardProject;
import com.mangban.blindboard.service.BoardProjectService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/blindboard/projects")
@RequiredArgsConstructor
public class BoardProjectController {

    private final BoardProjectService boardProjectService;

    @GetMapping
    public Result<List<BoardProject>> list() {
        return Result.success(boardProjectService.findAll());
    }

    @GetMapping("/{id}")
    public Result<BoardProject> getById(@PathVariable Long id) {
        return Result.success(boardProjectService.findById(id));
    }

    @PostMapping
    public Result<BoardProject> create(@RequestBody BoardProject project) {
        return Result.success(boardProjectService.create(project));
    }

    @PutMapping("/{id}")
    public Result<BoardProject> update(@PathVariable Long id, @RequestBody BoardProject project) {
        return Result.success(boardProjectService.update(id, project));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        boardProjectService.delete(id);
        return Result.success(null);
    }
}