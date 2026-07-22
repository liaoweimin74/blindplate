package com.mangban.location.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.LocationChangeRecord;
import com.mangban.location.repository.LocationChangeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationChangeRecordService {

    private final LocationChangeRecordRepository changeRecordRepository;

    public LocationChangeRecord createChangeRecord(Long locationId, String changeType,
                                                    String fieldName, String oldValue,
                                                    String newValue, Long applicantId) {
        LocationChangeRecord record = new LocationChangeRecord();
        record.setLocationId(locationId);
        record.setChangeType(changeType);
        record.setFieldName(fieldName);
        record.setOldValue(oldValue);
        record.setNewValue(newValue);
        record.setApplicantId(applicantId);
        record.setStatus("CREATE".equals(changeType) ? "APPROVED" : "PENDING");
        return changeRecordRepository.save(record);
    }

    public LocationChangeRecord approve(Long id, String comment, Long approverId, boolean isAdmin) {
        if (!isAdmin) {
            throw new BusinessException(403, "仅管理员可审批变更");
        }
        LocationChangeRecord record = changeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "变更记录不存在"));
        if (!"PENDING".equals(record.getStatus())) {
            throw new BusinessException(400, "该变更已处理");
        }
        record.setStatus("APPROVED");
        record.setApproverId(approverId);
        record.setApprovalComment(comment);
        record.setApprovedAt(LocalDateTime.now());
        return changeRecordRepository.save(record);
    }

    public LocationChangeRecord reject(Long id, String reason, Long approverId, boolean isAdmin) {
        if (!isAdmin) {
            throw new BusinessException(403, "仅管理员可审批变更");
        }
        LocationChangeRecord record = changeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "变更记录不存在"));
        if (!"PENDING".equals(record.getStatus())) {
            throw new BusinessException(400, "该变更已处理");
        }
        record.setStatus("REJECTED");
        record.setApproverId(approverId);
        record.setApprovalComment(reason);
        record.setApprovedAt(LocalDateTime.now());
        return changeRecordRepository.save(record);
    }

    public List<LocationChangeRecord> queryHistory(Long locationId, String status,
                                                     String changeType, Long applicantId,
                                                     LocalDateTime start, LocalDateTime end) {
        if (locationId != null && status != null) {
            return changeRecordRepository.findByLocationIdAndStatus(locationId, status);
        }
        if (locationId != null) {
            return changeRecordRepository.findByLocationIdOrderByCreatedAtDesc(locationId);
        }
        if (status != null) {
            return changeRecordRepository.findByStatus(status);
        }
        if (applicantId != null) {
            return changeRecordRepository.findByApplicantId(applicantId);
        }
        if (start != null && end != null) {
            return changeRecordRepository.findByTimeRange(start, end);
        }
        return changeRecordRepository.findAll();
    }
}
