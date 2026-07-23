package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateScrapRecord;
import com.mangban.blindplate.repository.BlindPlateScrapRecordRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final BlindPlateScrapRecordRepository scrapRecordRepository;
    private final BlindPlateService blindPlateService;

    public BlindPlateScrapRecord submitScrap(Long blindPlateId, String applicant, String reason) {
        BlindPlate plate = blindPlateService.findById(blindPlateId);
        if ("scrapped".equals(plate.getStatus())) {
            throw new BusinessException(400, "该盲板已报废，无法重复申请");
        }
        if (scrapRecordRepository.existsByBlindPlateIdAndStatus(blindPlateId, "pending")) {
            throw new BusinessException(400, "该盲板已有待审批的报废申请");
        }
        BlindPlateScrapRecord record = new BlindPlateScrapRecord();
        record.setBlindPlateId(blindPlateId);
        record.setApplicant(applicant);
        record.setReason(reason);
        record.setStatus("pending");
        record.setApplyTime(LocalDateTime.now());
        return scrapRecordRepository.save(record);
    }

    public BlindPlateScrapRecord approveScrap(Long scrapId, boolean approved, String approver, String comment) {
        BlindPlateScrapRecord record = scrapRecordRepository.findById(scrapId)
                .orElseThrow(() -> new BusinessException(404, "报废申请不存在"));
        record.setApprover(approver);
        record.setApproveTime(LocalDateTime.now());
        record.setApproveComment(comment);
        if (approved) {
            record.setStatus("approved");
            BlindPlate plate = blindPlateService.findById(record.getBlindPlateId());
            plate.setStatus("scrapped");
            blindPlateService.updateLifecycleStatus(plate.getId(), "scrapped");
            blindPlateService.update(plate.getId(), plate);
        } else {
            record.setStatus("rejected");
        }
        return scrapRecordRepository.save(record);
    }

    public Page<BlindPlateScrapRecord> findAll(String status, String applicant, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return scrapRecordRepository.findByStatus(status, pageable);
        }
        return scrapRecordRepository.findAll(pageable);
    }

    public List<BlindPlateScrapRecord> findByBlindPlateId(Long blindPlateId) {
        return scrapRecordRepository.findByBlindPlateIdOrderByApplyTimeDesc(blindPlateId);
    }
}