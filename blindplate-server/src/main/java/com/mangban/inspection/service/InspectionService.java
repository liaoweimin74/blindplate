package com.mangban.inspection.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.inspection.entity.InspectionItem;
import com.mangban.inspection.entity.InspectionPlan;
import com.mangban.inspection.entity.InspectionRecord;
import com.mangban.inspection.repository.InspectionItemRepository;
import com.mangban.inspection.repository.InspectionPlanRepository;
import com.mangban.inspection.repository.InspectionRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        record.setResult("PENDING");
        InspectionRecord savedRecord = recordRepository.save(record);

        items.forEach(item -> {
            item.setRecordId(savedRecord.getId());
            itemRepository.save(item);
        });

        return savedRecord;
    }

    public List<InspectionRecord> listRecords(Long planId) {
        return recordRepository.findByPlanId(planId);
    }
}
