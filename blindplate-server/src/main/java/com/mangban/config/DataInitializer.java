package com.mangban.config;

import com.mangban.auth.entity.User;
import com.mangban.auth.repository.UserRepository;
import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.IsolationPointDetailRepository;
import com.mangban.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlindPlateRepository blindPlateRepository;
    private final LocationRepository locationRepository;
    private final IsolationPointDetailRepository isolationPointDetailRepository;

    private static final Map<String, String> STATUS_MIGRATION = Map.of(
            "available", "in_stock",
            "installed", "in_use",
            "removed", "in_stock",
            "maintenance", "in_inspection"
    );

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            userRepository.save(admin);
            log.info("已创建默认管理员: admin/admin123");
        }

        // Migrate old status values
        List<BlindPlate> plates = blindPlateRepository.findAll();
        for (BlindPlate plate : plates) {
            String newStatus = STATUS_MIGRATION.get(plate.getStatus());
            if (newStatus != null) {
                plate.setStatus(newStatus);
                blindPlateRepository.save(plate);
                log.info("Migrated blind plate {} status: {} -> {}", plate.getCode(), plate.getStatus(), newStatus);
            }
        }
        if (!plates.isEmpty()) {
            log.info("Status migration completed for {} plates", plates.size());
        }

        if (locationRepository.count() == 0) {
            seedLocations();
        }
    }

    private void seedLocations() {
        Location factory = saveLoc("F-001", "第一化工厂", "FACTORY", null, 0);

        Location equip1 = saveLoc("EQ-001", "催化裂化装置", "EQUIPMENT", factory.getId(), 1);
        Location equip2 = saveLoc("EQ-002", "加氢精制装置", "EQUIPMENT", factory.getId(), 1);

        Location unit1 = saveLoc("U-001", "反应单元A", "UNIT", equip1.getId(), 2);
        Location unit2 = saveLoc("U-002", "分离单元B", "UNIT", equip1.getId(), 2);
        Location unit3 = saveLoc("U-003", "压缩单元C", "UNIT", equip2.getId(), 2);

        saveIsolationPoint("IP-001", "进料隔离点", unit1.getId(), 3, "氢气", "A", "法兰", 2.5, 200);
        saveIsolationPoint("IP-002", "出料隔离点", unit1.getId(), 3, "汽油", "B", "阀门", 1.6, 150);
        saveIsolationPoint("IP-003", "回流隔离点", unit2.getId(), 3, "液化气", "A", "法兰", 3.0, 180);
        saveIsolationPoint("IP-004", "入口隔离点", unit3.getId(), 3, "天然气", "C", "阀门", 4.0, 80);

        log.info("已初始化示例位置数据");
    }

    private Location saveLoc(String code, String name, String type, Long parentId, int level) {
        Location loc = new Location();
        loc.setCode(code);
        loc.setName(name);
        loc.setType(type);
        loc.setParentId(parentId);
        loc.setLevel(level);
        return locationRepository.save(loc);
    }

    private void saveIsolationPoint(String code, String name, Long parentId, int level,
                                     String medium, String hazardLevel, String isolationType,
                                     double pressure, double temperature) {
        Location loc = saveLoc(code, name, "ISOLATION_POINT", parentId, level);
        IsolationPointDetail detail = new IsolationPointDetail();
        detail.setLocation(loc);
        detail.setMedium(medium);
        detail.setHazardLevel(hazardLevel);
        detail.setIsolationType(isolationType);
        detail.setPressure(pressure);
        detail.setTemperature(temperature);
        isolationPointDetailRepository.save(detail);
    }
}
