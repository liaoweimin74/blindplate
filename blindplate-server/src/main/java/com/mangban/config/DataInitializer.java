package com.mangban.config;

import com.mangban.auth.entity.User;
import com.mangban.auth.repository.UserRepository;
import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
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
    }
}
