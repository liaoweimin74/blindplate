package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionScheduleService {

    private final BlindPlateRepository blindPlateRepository;
    private final BlindPlateService blindPlateService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkInspectionDue() {
        LocalDate today = LocalDate.now();
        List<BlindPlate> allPlates = blindPlateRepository.findAll();
        for (BlindPlate plate : allPlates) {
            if (plate.getNextInspectionDate() == null) continue;
            if ("scrapped".equals(plate.getLifecycleStatus())) continue;
            if (plate.getNextInspectionDate().isBefore(today)) {
                blindPlateService.updateLifecycleStatus(plate.getId(), "overdue");
            } else if (!plate.getNextInspectionDate().isAfter(today.plusDays(7))) {
                blindPlateService.updateLifecycleStatus(plate.getId(), "inspection_due");
            }
        }
        log.info("Inspection schedule check completed at {}", today);
    }
}