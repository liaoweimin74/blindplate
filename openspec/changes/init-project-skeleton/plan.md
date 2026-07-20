# 盲板管理系统项目骨架 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 创建盲板管理系统的完整项目骨架，包括后端 Spring Boot 项目和前端 Vue3 项目的基础结构

**Architecture:** 模块化单体架构，后端 Spring Boot 3.x + Spring Data JPA + MySQL，前端 Vue3 + TypeScript + Vite + Element Plus，通过 RESTful API 通信

**Tech Stack:** Java 17, Spring Boot 3.x, Spring Data JPA, MySQL 8.0, Vue3, TypeScript, Vite, Element Plus, Pinia, Axios

---

## File Structure

```
blindplate-server/
├── pom.xml
├── src/main/java/com/mangban/
│   ├── BlindPlateApplication.java
│   ├── common/
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   └── JpaConfig.java
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── result/
│   │       └── Result.java
│   ├── auth/
│   │   ├── controller/AuthController.java
│   │   ├── dto/LoginRequest.java
│   │   ├── dto/LoginResponse.java
│   │   ├── entity/User.java
│   │   ├── entity/Role.java
│   │   ├── entity/Menu.java
│   │   ├── repository/UserRepository.java
│   │   ├── repository/RoleRepository.java
│   │   ├── repository/MenuRepository.java
│   │   ├── service/AuthService.java
│   │   └── service/UserService.java
│   ├── blindplate/
│   │   ├── controller/BlindPlateController.java
│   │   ├── dto/BlindPlateDTO.java
│   │   ├── entity/BlindPlate.java
│   │   ├── repository/BlindPlateRepository.java
│   │   └── service/BlindPlateService.java
│   ├── location/
│   │   ├── controller/LocationController.java
│   │   ├── dto/LocationDTO.java
│   │   ├── entity/Location.java
│   │   ├── repository/LocationRepository.java
│   │   └── service/LocationService.java
│   ├── operation/
│   │   ├── controller/OperationController.java
│   │   ├── dto/OperationOrderDTO.java
│   │   ├── entity/OperationOrder.java
│   │   ├── entity/OperationRecord.java
│   │   ├── repository/OperationOrderRepository.java
│   │   ├── repository/OperationRecordRepository.java
│   │   └── service/OperationService.java
│   ├── inspection/
│   │   ├── controller/InspectionController.java
│   │   ├── dto/InspectionPlanDTO.java
│   │   ├── entity/InspectionPlan.java
│   │   ├── entity/InspectionRecord.java
│   │   ├── entity/InspectionItem.java
│   │   ├── repository/InspectionPlanRepository.java
│   │   ├── repository/InspectionRecordRepository.java
│   │   ├── repository/InspectionItemRepository.java
│   │   └── service/InspectionService.java
│   └── report/
│       ├── controller/ReportController.java
│       └── service/ReportService.java
├── src/main/resources/
│   └── application.yml
└── src/test/java/com/mangban/
    └── BlindPlateApplicationTests.java

blindplate-web/
├── index.html
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tsconfig.node.json
├── env.d.ts
├── src/
│   ├── main.ts
│   ├── App.vue
│   ├── api/
│   │   ├── request.ts
│   │   ├── auth.ts
│   │   ├── blindplate.ts
│   │   ├── location.ts
│   │   ├── operation.ts
│   │   ├── inspection.ts
│   │   └── report.ts
│   ├── assets/
│   │   └── logo.svg
│   ├── components/
│   │   └── common/
│   │       └── Pagination.vue
│   ├── router/
│   │   └── index.ts
│   ├── stores/
│   │   ├── auth.ts
│   │   └── app.ts
│   ├── types/
│   │   └── index.ts
│   ├── utils/
│   │   └── index.ts
│   └── views/
│       ├── auth/
│       │   └── Login.vue
│       ├── blindplate/
│       │   └── BlindPlateList.vue
│       ├── location/
│       │   └── LocationTree.vue
│       ├── operation/
│       │   └── OperationList.vue
│       ├── inspection/
│       │   └── InspectionList.vue
│       └── report/
│           └── Dashboard.vue
└── public/
    └── favicon.ico
```

---

## Task 1: 后端项目初始化

**Files:**
- Create: `blindplate-server/pom.xml`
- Create: `blindplate-server/src/main/java/com/mangban/BlindPlateApplication.java`
- Create: `blindplate-server/src/main/resources/application.yml`
- Create: `blindplate-server/src/test/java/com/mangban/BlindPlateApplicationTests.java`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.mangban</groupId>
    <artifactId>blindplate-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>blindplate-server</name>
    <description>盲板管理系统后端</description>
    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.5</jjwt.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建启动类**

```java
package com.mangban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlindPlateApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlindPlateApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blindplate_db?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8mb4
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect

jwt:
  secret: blindplate-secret-key-for-jwt-token-generation-2024
  expiration: 86400000

logging:
  level:
    com.mangban: DEBUG
```

- [ ] **Step 4: 创建测试类**

```java
package com.mangban;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlindPlateApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add blindplate-server/
git commit -m "feat: initialize Spring Boot project skeleton"
```

---

## Task 2: 公共模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/common/result/Result.java`
- Create: `blindplate-server/src/main/java/com/mangban/common/exception/BusinessException.java`
- Create: `blindplate-server/src/main/java/com/mangban/common/exception/GlobalExceptionHandler.java`
- Create: `blindplate-server/src/main/java/com/mangban/common/config/CorsConfig.java`

- [ ] **Step 1: 创建 Result.java**

```java
package com.mangban.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

- [ ] **Step 2: 创建 BusinessException.java**

```java
package com.mangban.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

- [ ] **Step 3: 创建 GlobalExceptionHandler.java**

```java
package com.mangban.common.exception;

import com.mangban.common.result.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return Result.error(400, message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.error(500, "服务器内部错误");
    }
}
```

- [ ] **Step 4: 创建 CorsConfig.java**

```java
package com.mangban.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/common/
git commit -m "feat: add common modules (Result, exception handling, CORS)"
```

---

## Task 3: 用户认证模块 - Entity 和 Repository

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/auth/entity/User.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/entity/Role.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/entity/Menu.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/repository/UserRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/repository/RoleRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/repository/MenuRepository.java`

- [ ] **Step 1: 创建 User.java**

```java
package com.mangban.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "sys_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    private Integer status = 1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 Role.java**

```java
package com.mangban.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "sys_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(length = 200)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_role_menu",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_id")
    )
    private Set<Menu> menus;
}
```

- [ ] **Step 3: 创建 Menu.java**

```java
package com.mangban.auth.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sys_menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 100)
    private String path;

    @Column(length = 100)
    private String component;

    @Column(length = 50)
    private String icon;

    private Integer sort = 0;
}
```

- [ ] **Step 4: 创建 Repository 接口**

```java
package com.mangban.auth.repository;

import com.mangban.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

```java
package com.mangban.auth.repository;

import com.mangban.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByCode(String code);
}
```

```java
package com.mangban.auth.repository;

import com.mangban.auth.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByParentIdOrderBySort(Long parentId);
}
```

- [ ] **Step 5: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/auth/
git commit -m "feat: add auth module entities and repositories"
```

---

## Task 4: 用户认证模块 - JWT 和 Service

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/auth/util/JwtUtil.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/service/AuthService.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/dto/LoginRequest.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/dto/LoginResponse.java`

- [ ] **Step 1: 创建 JwtUtil.java**

```java
package com.mangban.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, String username) {
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }
}
```

- [ ] **Step 2: 创建 LoginRequest.java 和 LoginResponse.java**

```java
package com.mangban.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

```java
package com.mangban.auth.dto;

import lombok.Data;
import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
    private String username;
    private String name;
    private List<String> roles;
}
```

- [ ] **Step 3: 创建 AuthService.java**

```java
package com.mangban.auth.service;

import com.mangban.auth.dto.LoginRequest;
import com.mangban.auth.dto.LoginResponse;
import com.mangban.auth.entity.User;
import com.mangban.auth.repository.UserRepository;
import com.mangban.auth.util.JwtUtil;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getCode())
                .collect(Collectors.toList()));

        return response;
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/auth/util/
git add blindplate-server/src/main/java/com/mangban/auth/service/
git add blindplate-server/src/main/java/com/mangban/auth/dto/
git commit -m "feat: add JWT utility and auth service"
```

---

## Task 5: 用户认证模块 - Controller

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/auth/controller/AuthController.java`
- Create: `blindplate-server/src/main/java/com/mangban/auth/controller/UserController.java`

- [ ] **Step 1: 创建 AuthController.java**

```java
package com.mangban.auth.controller;

import com.mangban.auth.dto.LoginRequest;
import com.mangban.auth.dto.LoginResponse;
import com.mangban.auth.service.AuthService;
import com.mangban.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
```

- [ ] **Step 2: 创建 UserController.java（基础 CRUD）**

```java
package com.mangban.auth.controller;

import com.mangban.auth.entity.User;
import com.mangban.auth.repository.UserRepository;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(userRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))));
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/auth/controller/
git commit -m "feat: add auth and user controllers"
```

---

## Task 6: 盲板信息管理模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/blindplate/entity/BlindPlate.java`
- Create: `blindplate-server/src/main/java/com/mangban/blindplate/repository/BlindPlateRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/blindplate/dto/BlindPlateDTO.java`
- Create: `blindplate-server/src/main/java/com/mangban/blindplate/service/BlindPlateService.java`
- Create: `blindplate-server/src/main/java/com/mangban/blindplate/controller/BlindPlateController.java`

- [ ] **Step 1: 创建 BlindPlate.java**

```java
package com.mangban.blindplate.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "blindplate")
public class BlindPlate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String spec;

    @Column(length = 50)
    private String material;

    private Double diameter;

    private Double pressure;

    @Column(length = 100)
    private String manufacturer;

    @Column(nullable = false, length = 20)
    private String status = "available";

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
```

- [ ] **Step 2: 创建 BlindPlateRepository.java**

```java
package com.mangban.blindplate.repository;

import com.mangban.blindplate.entity.BlindPlate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlindPlateRepository extends JpaRepository<BlindPlate, Long> {
    boolean existsByCode(String code);

    @Query("SELECT b FROM BlindPlate b WHERE " +
           "(:keyword IS NULL OR b.code LIKE %:keyword% OR b.name LIKE %:keyword%) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:material IS NULL OR b.material = :material)")
    Page<BlindPlate> findByFilters(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("material") String material,
            Pageable pageable);
}
```

- [ ] **Step 3: 创建 BlindPlateDTO.java**

```java
package com.mangban.blindplate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlindPlateDTO {
    private Long id;

    @NotBlank(message = "盲板编号不能为空")
    private String code;

    @NotBlank(message = "盲板名称不能为空")
    private String name;

    private String spec;
    private String material;
    private Double diameter;
    private Double pressure;
    private String manufacturer;
    private String remark;
}
```

- [ ] **Step 4: 创建 BlindPlateService.java**

```java
package com.mangban.blindplate.service;

import com.mangban.blindplate.dto.BlindPlateDTO;
import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindPlateService {

    private final BlindPlateRepository blindPlateRepository;

    public Page<BlindPlate> list(String keyword, String status, String material, int page, int size) {
        return blindPlateRepository.findByFilters(
                keyword, status, material,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public BlindPlate create(BlindPlateDTO dto) {
        if (blindPlateRepository.existsByCode(dto.getCode())) {
            throw new BusinessException(400, "盲板编号已存在");
        }

        BlindPlate blindPlate = new BlindPlate();
        blindPlate.setCode(dto.getCode());
        blindPlate.setName(dto.getName());
        blindPlate.setSpec(dto.getSpec());
        blindPlate.setMaterial(dto.getMaterial());
        blindPlate.setDiameter(dto.getDiameter());
        blindPlate.setPressure(dto.getPressure());
        blindPlate.setManufacturer(dto.getManufacturer());
        blindPlate.setRemark(dto.getRemark());

        return blindPlateRepository.save(blindPlate);
    }

    public BlindPlate getById(Long id) {
        return blindPlateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "盲板不存在"));
    }

    public BlindPlate update(Long id, BlindPlateDTO dto) {
        BlindPlate blindPlate = getById(id);
        blindPlate.setName(dto.getName());
        blindPlate.setSpec(dto.getSpec());
        blindPlate.setMaterial(dto.getMaterial());
        blindPlate.setDiameter(dto.getDiameter());
        blindPlate.setPressure(dto.getPressure());
        blindPlate.setManufacturer(dto.getManufacturer());
        blindPlate.setRemark(dto.getRemark());
        return blindPlateRepository.save(blindPlate);
    }

    public void delete(Long id) {
        BlindPlate blindPlate = getById(id);
        blindPlateRepository.delete(blindPlate);
    }
}
```

- [ ] **Step 5: 创建 BlindPlateController.java**

```java
package com.mangban.blindplate.controller;

import com.mangban.blindplate.dto.BlindPlateDTO;
import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.service.BlindPlateService;
import com.mangban.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blindplates")
@RequiredArgsConstructor
public class BlindPlateController {

    private final BlindPlateService blindPlateService;

    @GetMapping
    public Result<Page<BlindPlate>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String material,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(blindPlateService.list(keyword, status, material, page, size));
    }

    @PostMapping
    public Result<BlindPlate> create(@Valid @RequestBody BlindPlateDTO dto) {
        return Result.success(blindPlateService.create(dto));
    }

    @GetMapping("/{id}")
    public Result<BlindPlate> getById(@PathVariable Long id) {
        return Result.success(blindPlateService.getById(id));
    }

    @PutMapping("/{id}")
    public Result<BlindPlate> update(@PathVariable Long id, @Valid @RequestBody BlindPlateDTO dto) {
        return Result.success(blindPlateService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        blindPlateService.delete(id);
        return Result.success(null);
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/blindplate/
git commit -m "feat: add blindplate management module"
```

---

## Task 7: 位置管理模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/location/entity/Location.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/repository/LocationRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/dto/LocationDTO.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/service/LocationService.java`
- Create: `blindplate-server/src/main/java/com/mangban/location/controller/LocationController.java`

- [ ] **Step 1: 创建 Location.java**

```java
package com.mangban.location.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(nullable = false, length = 20)
    private String type;

    @Transient
    private List<Location> children;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

- [ ] **Step 2: 创建 LocationRepository.java**

```java
package com.mangban.location.repository;

import com.mangban.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByParentIdOrderByCreatedAt(Long parentId);
    List<Location> findByTypeOrderByCreatedAt(String type);
}
```

- [ ] **Step 3: 创建 LocationDTO.java**

```java
package com.mangban.location.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    private Long parentId;

    @NotBlank(message = "位置名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "位置类型不能为空")
    private String type;
}
```

- [ ] **Step 4: 创建 LocationService.java**

```java
package com.mangban.location.service;

import com.mangban.location.dto.LocationDTO;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.LocationRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> getTree() {
        List<Location> roots = locationRepository.findByParentIdOrderByCreatedAt(null);
        roots.forEach(root -> root.setChildren(getChildren(root.getId())));
        return roots;
    }

    private List<Location> getChildren(Long parentId) {
        List<Location> children = locationRepository.findByParentIdOrderByCreatedAt(parentId);
        children.forEach(child -> child.setChildren(getChildren(child.getId())));
        return children;
    }

    public Location create(LocationDTO dto) {
        Location location = new Location();
        location.setParentId(dto.getParentId());
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        location.setType(dto.getType());
        return locationRepository.save(location);
    }

    public Location update(Long id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "位置不存在"));
        location.setName(dto.getName());
        location.setDescription(dto.getDescription());
        return locationRepository.save(location);
    }

    public void delete(Long id) {
        List<Location> children = locationRepository.findByParentIdOrderByCreatedAt(id);
        if (!children.isEmpty()) {
            throw new BusinessException(400, "该位置下存在子节点，无法删除");
        }
        locationRepository.deleteById(id);
    }
}
```

- [ ] **Step 5: 创建 LocationController.java**

```java
package com.mangban.location.controller;

import com.mangban.location.dto.LocationDTO;
import com.mangban.location.entity.Location;
import com.mangban.location.service.LocationService;
import com.mangban.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public Result<List<Location>> getTree() {
        return Result.success(locationService.getTree());
    }

    @PostMapping
    public Result<Location> create(@Valid @RequestBody LocationDTO dto) {
        return Result.success(locationService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<Location> update(@PathVariable Long id, @Valid @RequestBody LocationDTO dto) {
        return Result.success(locationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return Result.success(null);
    }
}
```

- [ ] **Step 6: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/location/
git commit -m "feat: add location management module"
```

---

## Task 8: 安装/拆除管理模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/operation/entity/OperationOrder.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/entity/OperationRecord.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/repository/OperationOrderRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/repository/OperationRecordRepository.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/dto/OperationOrderDTO.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/service/OperationService.java`
- Create: `blindplate-server/src/main/java/com/mangban/operation/controller/OperationController.java`

- [ ] **Step 1: 创建 Entity 类**

```java
package com.mangban.operation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_order")
public class OperationOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String orderNo;

    @Column(nullable = false, length = 20)
    private String type;

    private Long blindplateId;
    private Long locationId;
    private Long operatorId;
    private Long supervisorId;

    @Column(nullable = false, length = 20)
    private String status = "pending";

    private LocalDateTime plannedDate;
    private LocalDateTime actualDate;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

```java
package com.mangban.operation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_record")
public class OperationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Column(nullable = false, length = 20)
    private String action;

    private Long operatorId;

    private LocalDateTime operateTime;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

- [ ] **Step 2: 创建 Repository 接口**

```java
package com.mangban.operation.repository;

import com.mangban.operation.entity.OperationOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationOrderRepository extends JpaRepository<OperationOrder, Long> {
    Page<OperationOrder> findByStatus(String status, Pageable pageable);
}
```

```java
package com.mangban.operation.repository;

import com.mangban.operation.entity.OperationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperationRecordRepository extends JpaRepository<OperationRecord, Long> {
    List<OperationRecord> findByOrderIdOrderByOperateTimeDesc(Long orderId);
}
```

- [ ] **Step 3: 创建 DTO 和 Service**

```java
package com.mangban.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationOrderDTO {
    private Long id;

    @NotBlank(message = "操作类型不能为空")
    private String type;

    private Long blindplateId;
    private Long locationId;
    private Long operatorId;
    private LocalDateTime plannedDate;
    private String remark;
}
```

```java
package com.mangban.operation.service;

import com.mangban.operation.dto.OperationOrderDTO;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.entity.OperationRecord;
import com.mangban.operation.repository.OperationOrderRepository;
import com.mangban.operation.repository.OperationRecordRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationOrderRepository orderRepository;
    private final OperationRecordRepository recordRepository;

    public Page<OperationOrder> list(String status, int page, int size) {
        if (status != null) {
            return orderRepository.findByStatus(status,
                    PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        }
        return orderRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @Transactional
    public OperationOrder create(OperationOrderDTO dto) {
        OperationOrder order = new OperationOrder();
        order.setOrderNo(generateOrderNo(dto.getType()));
        order.setType(dto.getType());
        order.setBlindplateId(dto.getBlindplateId());
        order.setLocationId(dto.getLocationId());
        order.setOperatorId(dto.getOperatorId());
        order.setPlannedDate(dto.getPlannedDate());
        order.setRemark(dto.getRemark());
        return orderRepository.save(order);
    }

    public OperationOrder approve(Long id, Long supervisorId) {
        OperationOrder order = getById(id);
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(400, "该工单状态不允许审批");
        }
        order.setStatus("approved");
        order.setSupervisorId(supervisorId);
        return orderRepository.save(order);
    }

    @Transactional
    public OperationOrder complete(Long id) {
        OperationOrder order = getById(id);
        if (!"approved".equals(order.getStatus())) {
            throw new BusinessException(400, "该工单未审批或已完成");
        }
        order.setStatus("completed");
        order.setActualDate(LocalDateTime.now());

        OperationRecord record = new OperationRecord();
        record.setOrderId(order.getId());
        record.setAction(order.getType());
        record.setOperatorId(order.getOperatorId());
        record.setOperateTime(LocalDateTime.now());
        recordRepository.save(record);

        return orderRepository.save(order);
    }

    private OperationOrder getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "工单不存在"));
    }

    private String generateOrderNo(String type) {
        String prefix = switch (type) {
            case "install" -> "INST";
            case "remove" -> "RMVL";
            case "restore" -> "RSTR";
            default -> "OP";
        };
        return prefix + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
```

- [ ] **Step 4: 创建 Controller**

```java
package com.mangban.operation.controller;

import com.mangban.operation.dto.OperationOrderDTO;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.service.OperationService;
import com.mangban.common.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operations")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    @GetMapping
    public Result<Page<OperationOrder>> list(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(operationService.list(status, page, size));
    }

    @PostMapping
    public Result<OperationOrder> create(@Valid @RequestBody OperationOrderDTO dto) {
        return Result.success(operationService.create(dto));
    }

    @PutMapping("/{id}/approve")
    public Result<OperationOrder> approve(@PathVariable Long id) {
        return Result.success(operationService.approve(id, 1L));
    }

    @PutMapping("/{id}/complete")
    public Result<OperationOrder> complete(@PathVariable Long id) {
        return Result.success(operationService.complete(id));
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/operation/
git commit -m "feat: add operation management module"
```

---

## Task 9: 巡检管理模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/inspection/entity/InspectionPlan.java`
- Create: `blindplate-server/src/main/java/com/mangban/inspection/entity/InspectionRecord.java`
- Create: `blindplate-server/src/main/java/com/mangban/inspection/entity/InspectionItem.java`
- Create: `blindplate-server/src/main/java/com/mangban/inspection/repository/*.java`
- Create: `blindplate-server/src/main/java/com/mangban/inspection/service/InspectionService.java`
- Create: `blindplate-server/src/main/java/com/mangban/inspection/controller/InspectionController.java`

- [ ] **Step 1: 创建 Entity 类**

```java
package com.mangban.inspection.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_plan")
public class InspectionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String cycle;

    private Long responsibleId;

    @Column(nullable = false, length = 20)
    private String status = "active";

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

```java
package com.mangban.inspection.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_record")
public class InspectionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long planId;
    private Long inspectorId;

    private LocalDateTime inspectTime;

    @Column(nullable = false, length = 20)
    private String result;

    @Column(length = 500)
    private String remark;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

```java
package com.mangban.inspection.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inspection_item")
public class InspectionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recordId;
    private Long blindplateId;

    @Column(length = 100)
    private String checkItem;

    @Column(nullable = false, length = 20)
    private String checkResult;

    @Column(length = 500)
    private String abnormalDesc;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

- [ ] **Step 2: 创建 Repository 和 Service**

```java
package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionPlanRepository extends JpaRepository<InspectionPlan, Long> {
    List<InspectionPlan> findByStatus(String status);
}
```

```java
package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {
    List<InspectionRecord> findByPlanIdOrderByInspectTimeDesc(Long planId);
}
```

```java
package com.mangban.inspection.repository;

import com.mangban.inspection.entity.InspectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionItemRepository extends JpaRepository<InspectionItem, Long> {
    List<InspectionItem> findByRecordId(Long recordId);
}
```

```java
package com.mangban.inspection.service;

import com.mangban.inspection.entity.*;
import com.mangban.inspection.repository.*;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionPlanRepository planRepository;
    private final InspectionRecordRepository recordRepository;
    private final InspectionItemRepository itemRepository;

    public List<InspectionPlan> listPlans() {
        return planRepository.findAll();
    }

    public InspectionPlan createPlan(InspectionPlan plan) {
        return planRepository.save(plan);
    }

    @Transactional
    public InspectionRecord executeInspection(Long planId, Long inspectorId, List<InspectionItem> items) {
        InspectionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new BusinessException(404, "巡检计划不存在"));

        InspectionRecord record = new InspectionRecord();
        record.setPlanId(planId);
        record.setInspectorId(inspectorId);
        record.setInspectTime(LocalDateTime.now());

        boolean hasAbnormal = items.stream()
                .anyMatch(item -> "abnormal".equals(item.getCheckResult()));
        record.setResult(hasAbnormal ? "abnormal" : "normal");

        record = recordRepository.save(record);

        Long recordId = record.getId();
        items.forEach(item -> {
            item.setRecordId(recordId);
            itemRepository.save(item);
        });

        return record;
    }

    public List<InspectionRecord> listRecords(Long planId) {
        return recordRepository.findByPlanIdOrderByInspectTimeDesc(planId);
    }
}
```

- [ ] **Step 3: 创建 Controller**

```java
package com.mangban.inspection.controller;

import com.mangban.inspection.entity.*;
import com.mangban.inspection.service.InspectionService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    @GetMapping
    public Result<List<InspectionPlan>> listPlans() {
        return Result.success(inspectionService.listPlans());
    }

    @PostMapping
    public Result<InspectionPlan> createPlan(@RequestBody InspectionPlan plan) {
        return Result.success(inspectionService.createPlan(plan));
    }

    @PostMapping("/{id}/execute")
    public Result<InspectionRecord> execute(
            @PathVariable Long id,
            @RequestParam Long inspectorId,
            @RequestBody List<InspectionItem> items) {
        return Result.success(inspectionService.executeInspection(id, inspectorId, items));
    }

    @GetMapping("/records")
    public Result<List<InspectionRecord>> listRecords(@RequestParam Long planId) {
        return Result.success(inspectionService.listRecords(planId));
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/inspection/
git commit -m "feat: add inspection management module"
```

---

## Task 10: 报表统计模块

**Files:**
- Create: `blindplate-server/src/main/java/com/mangban/report/controller/ReportController.java`
- Create: `blindplate-server/src/main/java/com/mangban/report/service/ReportService.java`

- [ ] **Step 1: 创建 ReportService.java**

```java
package com.mangban.report.service;

import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.operation.repository.OperationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BlindPlateRepository blindPlateRepository;
    private final OperationOrderRepository orderRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBlindPlates", blindPlateRepository.count());
        return stats;
    }
}
```

- [ ] **Step 2: 创建 ReportController.java**

```java
package com.mangban.report.controller;

import com.mangban.report.service.ReportService;
import com.mangban.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
```

- [ ] **Step 3: Commit**

```bash
git add blindplate-server/src/main/java/com/mangban/report/
git commit -m "feat: add report statistics module"
```

---

## Task 11: 前端项目初始化

**Files:**
- Create: `blindplate-web/package.json`
- Create: `blindplate-web/vite.config.ts`
- Create: `blindplate-web/tsconfig.json`
- Create: `blindplate-web/index.html`
- Create: `blindplate-web/src/main.ts`
- Create: `blindplate-web/src/App.vue`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "blindplate-web",
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vue-tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.21",
    "vue-router": "^4.3.0",
    "pinia": "^2.1.7",
    "axios": "^1.6.8",
    "element-plus": "^2.6.3",
    "@element-plus/icons-vue": "^2.3.1"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "typescript": "^5.4.5",
    "vite": "^5.2.8",
    "vue-tsc": "^2.0.11"
  }
}
```

- [ ] **Step 2: 创建 vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] **Step 4: 创建 index.html 和 main.ts**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <link rel="icon" type="image/svg+xml" href="/favicon.ico" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>盲板管理系统</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

```typescript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(ElementPlus)
app.mount('#app')
```

- [ ] **Step 5: 创建 App.vue**

```vue
<template>
  <router-view />
</template>

<script setup lang="ts">
</script>
```

- [ ] **Step 6: Commit**

```bash
git add blindplate-web/
git commit -m "feat: initialize Vue3 project skeleton"
```

---

## Task 12: 前端路由和状态管理

**Files:**
- Create: `blindplate-web/src/router/index.ts`
- Create: `blindplate-web/src/stores/auth.ts`
- Create: `blindplate-web/src/stores/app.ts`
- Create: `blindplate-web/src/types/index.ts`

- [ ] **Step 1: 创建 router/index.ts**

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue')
  },
  {
    path: '/',
    redirect: '/blindplates'
  },
  {
    path: '/blindplates',
    name: 'BlindPlateList',
    component: () => import('@/views/blindplate/BlindPlateList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/locations',
    name: 'LocationTree',
    component: () => import('@/views/location/LocationTree.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/operations',
    name: 'OperationList',
    component: () => import('@/views/operation/OperationList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/inspections',
    name: 'InspectionList',
    component: () => import('@/views/inspection/InspectionList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/reports',
    name: 'Dashboard',
    component: () => import('@/views/report/Dashboard.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

- [ ] **Step 2: 创建 stores/auth.ts**

```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<any>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, setToken, logout }
})
```

- [ ] **Step 3: 创建 types/index.ts**

```typescript
export interface Result<T> {
  code: number
  message: string
  data: T
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface User {
  id: number
  username: string
  name: string
  phone: string
  status: number
}

export interface BlindPlate {
  id: number
  code: string
  name: string
  spec: string
  material: string
  diameter: number
  pressure: number
  manufacturer: string
  status: string
  remark: string
  createdAt: string
}

export interface Location {
  id: number
  parentId: number | null
  name: string
  description: string
  type: string
  children?: Location[]
}

export interface OperationOrder {
  id: number
  orderNo: string
  type: string
  blindplateId: number
  locationId: number
  status: string
  plannedDate: string
  actualDate: string
  remark: string
  createdAt: string
}
```

- [ ] **Step 4: Commit**

```bash
git add blindplate-web/src/router/
git add blindplate-web/src/stores/
git add blindplate-web/src/types/
git commit -m "feat: add router, stores, and type definitions"
```

---

## Task 13: 前端 API 封装和页面组件

**Files:**
- Create: `blindplate-web/src/api/request.ts`
- Create: `blindplate-web/src/api/auth.ts`
- Create: `blindplate-web/src/api/blindplate.ts`
- Create: `blindplate-web/src/views/auth/Login.vue`
- Create: `blindplate-web/src/views/blindplate/BlindPlateList.vue`

- [ ] **Step 1: 创建 api/request.ts**

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || '请求失败'
    ElMessage.error(message)
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default request
```

- [ ] **Step 2: 创建 api/auth.ts 和 api/blindplate.ts**

```typescript
import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}
```

```typescript
import request from './request'
import type { BlindPlate, Page } from '@/types'

export function getBlindPlates(params: any) {
  return request.get('/blindplates', { params })
}

export function createBlindPlate(data: Partial<BlindPlate>) {
  return request.post('/blindplates', data)
}

export function updateBlindPlate(id: number, data: Partial<BlindPlate>) {
  return request.put(`/blindplates/${id}`, data)
}

export function deleteBlindPlate(id: number) {
  return request.delete(`/blindplates/${id}`)
}
```

- [ ] **Step 3: 创建 Login.vue**

```vue
<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>盲板管理系统</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" />
        </el-form-item>
        <el-button type="primary" @click="handleLogin" :loading="loading">
          登录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { login } from '@/api/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res: any = await login(form.value)
    authStore.setToken(res.data.token)
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.login-card {
  width: 400px;
}
</style>
```

- [ ] **Step 4: 创建 BlindPlateList.vue**

```vue
<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>盲板列表</span>
          <el-button type="primary" @click="showDialog()">新增盲板</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="编号/名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable placeholder="全部">
            <el-option label="可用" value="available" />
            <el-option label="已安装" value="installed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="code" label="编号" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="spec" label="规格" />
        <el-table-column prop="material" label="材质" />
        <el-table-column prop="status" label="状态" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBlindPlates, deleteBlindPlate } from '@/api/blindplate'

const loading = ref(false)
const tableData = ref<any[]>([])
const searchForm = ref({ keyword: '', status: '' })
const pagination = ref({ page: 1, size: 20, total: 0 })

async function loadData() {
  loading.value = true
  try {
    const res: any = await getBlindPlates({
      ...searchForm.value,
      page: pagination.value.page - 1,
      size: pagination.value.size
    })
    tableData.value = res.data.content
    pagination.value.total = res.data.totalElements
  } finally {
    loading.value = false
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确认删除？', '提示')
  await deleteBlindPlate(id)
  ElMessage.success('删除成功')
  loadData()
}

function showDialog(row?: any) {
  // TODO: 打开新增/编辑弹窗
}

onMounted(loadData)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
```

- [ ] **Step 5: Commit**

```bash
git add blindplate-web/src/api/
git add blindplate-web/src/views/
git commit -m "feat: add API wrappers and page components"
```

---

## Task 14: 数据库初始化脚本

**Files:**
- Create: `blindplate-server/src/main/resources/data.sql`

- [ ] **Step 1: 创建初始数据脚本**

```sql
-- 初始角色
INSERT INTO sys_role (name, code, description) VALUES
('管理员', 'admin', '系统管理员，拥有所有权限'),
('班组长', 'team_leader', '班组长，负责审批和巡检管理'),
('操作员', 'operator', '操作员，负责盲板安装/拆除操作'),
('巡检员', 'inspector', '巡检员，负责定期巡检');

-- 初始管理员用户 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (username, password, name, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 管理员角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
```

- [ ] **Step 2: Commit**

```bash
git add blindplate-server/src/main/resources/data.sql
git commit -m "feat: add initial data SQL script"
```

---

**Plan complete and saved to `openspec/changes/init-project-skeleton/plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
