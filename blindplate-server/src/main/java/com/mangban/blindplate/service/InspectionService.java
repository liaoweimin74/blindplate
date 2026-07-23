package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateInspection;
import com.mangban.blindplate.repository.BlindPlateInspectionRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service("blindPlateInspectionService")
@RequiredArgsConstructor
public class InspectionService {

    private final BlindPlateInspectionRepository inspectionRepository;
    private final BlindPlateService blindPlateService;

    public BlindPlateInspection create(Long blindPlateId, BlindPlateInspection inspection) {
        BlindPlate plate = blindPlateService.findById(blindPlateId);
        inspection.setId(null);
        inspection.setBlindPlateId(blindPlateId);
        BlindPlateInspection saved = inspectionRepository.save(inspection);

        // Update BlindPlate.nextInspectionDate from inspection record
        plate.setNextInspectionDate(inspection.getNextInspectionDate());
        blindPlateService.update(plate.getId(), plate);

        // Recalculate lifecycle status using updated plate
        recalculateLifecycleStatus(blindPlateService.findById(blindPlateId));
        return saved;
    }

    public List<BlindPlateInspection> findByBlindPlateId(Long blindPlateId) {
        return inspectionRepository.findByBlindPlateIdOrderByInspectionDateDesc(blindPlateId);
    }

    public BlindPlateInspection update(Long id, BlindPlateInspection inspection) {
        BlindPlateInspection existing = inspectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "检查记录不存在"));
        if (inspection.getInspectionDate() != null) existing.setInspectionDate(inspection.getInspectionDate());
        if (inspection.getResult() != null) existing.setResult(inspection.getResult());
        if (inspection.getNextInspectionDate() != null) existing.setNextInspectionDate(inspection.getNextInspectionDate());
        if (inspection.getInspector() != null) existing.setInspector(inspection.getInspector());
        if (inspection.getRemark() != null) existing.setRemark(inspection.getRemark());
        BlindPlateInspection saved = inspectionRepository.save(existing);

        // Sync plate's nextInspectionDate with the updated inspection record
        BlindPlate plate = blindPlateService.findById(existing.getBlindPlateId());
        plate.setNextInspectionDate(existing.getNextInspectionDate());
        blindPlateService.update(plate.getId(), plate);

        // Recalculate lifecycle
        recalculateLifecycleStatus(blindPlateService.findById(existing.getBlindPlateId()));
        return saved;
    }

    public void delete(Long id) {
        BlindPlateInspection existing = inspectionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "检查记录不存在"));
        Long blindPlateId = existing.getBlindPlateId();
        inspectionRepository.deleteById(id);
        // Recalculate lifecycle - plate's nextInspectionDate may need updating from remaining records
        BlindPlate plate = blindPlateService.findById(blindPlateId);
        List<BlindPlateInspection> remaining = inspectionRepository
                .findByBlindPlateIdOrderByInspectionDateDesc(blindPlateId);
        if (remaining.isEmpty()) {
            plate.setNextInspectionDate(null);
        } else {
            plate.setNextInspectionDate(remaining.get(0).getNextInspectionDate());
        }
        blindPlateService.update(plate.getId(), plate);
        recalculateLifecycleStatus(blindPlateService.findById(blindPlateId));
    }

    private void recalculateLifecycleStatus(BlindPlate plate) {
        LocalDate today = LocalDate.now();
        if (plate.getNextInspectionDate() == null) {
            blindPlateService.updateLifecycleStatus(plate.getId(), "normal");
            return;
        }
        if (plate.getNextInspectionDate().isBefore(today)) {
            blindPlateService.updateLifecycleStatus(plate.getId(), "overdue");
        } else if (!plate.getNextInspectionDate().isAfter(today.plusDays(7))) {
            blindPlateService.updateLifecycleStatus(plate.getId(), "inspection_due");
        } else {
            blindPlateService.updateLifecycleStatus(plate.getId(), "normal");
        }
    }
}