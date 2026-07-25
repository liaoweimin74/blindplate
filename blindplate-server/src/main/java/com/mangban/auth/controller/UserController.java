package com.mangban.auth.controller;

import com.mangban.auth.entity.User;
import com.mangban.auth.repository.UserRepository;
import com.mangban.common.exception.BusinessException;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<List<User>> list() {
        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return Result.success(users);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword() != null ? user.getPassword() : "123456"));
        User saved = userRepository.save(user);
        saved.setPassword(null);
        return Result.success(saved);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        existing.setName(user.getName());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        existing.setRole(user.getRole());
        existing.setStatus(user.getStatus());
        User saved = userRepository.save(existing);
        saved.setPassword(null);
        return Result.success(saved);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return Result.success(null);
    }

    @PostMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        return Result.success(null);
    }
}
