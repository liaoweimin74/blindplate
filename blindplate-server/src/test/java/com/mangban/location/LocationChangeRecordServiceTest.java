package com.mangban.location;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.LocationChangeRecord;
import com.mangban.location.repository.LocationChangeRecordRepository;
import com.mangban.location.service.LocationChangeRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationChangeRecordServiceTest {

    @Mock
    private LocationChangeRecordRepository changeRecordRepository;

    @InjectMocks
    private LocationChangeRecordService changeRecordService;

    private LocationChangeRecord createPendingRecord() {
        LocationChangeRecord r = new LocationChangeRecord();
        r.setId(1L);
        r.setLocationId(10L);
        r.setChangeType("UPDATE");
        r.setFieldName("name");
        r.setOldValue("old");
        r.setNewValue("new");
        r.setStatus("PENDING");
        r.setApplicantId(100L);
        return r;
    }

    @Test
    void approve_byNonAdmin_throws() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRecordService.approve(1L, "ok", 1L, false));
        assertEquals(403, ex.getCode());
        assertEquals("仅管理员可审批变更", ex.getMessage());
    }

    @Test
    void approve_byAdmin_succeeds() {
        LocationChangeRecord record = createPendingRecord();
        when(changeRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(changeRecordRepository.save(any(LocationChangeRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationChangeRecord result = changeRecordService.approve(1L, "approved", 2L, true);

        assertEquals("APPROVED", result.getStatus());
        assertEquals(2L, result.getApproverId());
        assertNotNull(result.getApprovedAt());
        verify(changeRecordRepository).save(any(LocationChangeRecord.class));
    }

    @Test
    void approve_alreadyApproved_throws() {
        LocationChangeRecord record = createPendingRecord();
        record.setStatus("APPROVED");
        when(changeRecordRepository.findById(1L)).thenReturn(Optional.of(record));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> changeRecordService.approve(1L, "ok", 2L, true));
        assertEquals(400, ex.getCode());
        assertEquals("该变更已处理", ex.getMessage());
    }

    @Test
    void reject_byAdmin_succeeds() {
        LocationChangeRecord record = createPendingRecord();
        when(changeRecordRepository.findById(1L)).thenReturn(Optional.of(record));
        when(changeRecordRepository.save(any(LocationChangeRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationChangeRecord result = changeRecordService.reject(1L, "bad change", 2L, true);

        assertEquals("REJECTED", result.getStatus());
        assertEquals(2L, result.getApproverId());
        assertNotNull(result.getApprovedAt());
    }

    @Test
    void createChangeRecord_createType_autoApproved() {
        when(changeRecordRepository.save(any(LocationChangeRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationChangeRecord result = changeRecordService.createChangeRecord(1L, "CREATE", "*", null, "snapshot", 100L);

        assertEquals("APPROVED", result.getStatus());
        assertEquals("CREATE", result.getChangeType());
    }

    @Test
    void createChangeRecord_updateType_pending() {
        when(changeRecordRepository.save(any(LocationChangeRecord.class))).thenAnswer(inv -> inv.getArgument(0));

        LocationChangeRecord result = changeRecordService.createChangeRecord(1L, "UPDATE", "name", "old", "new", 100L);

        assertEquals("PENDING", result.getStatus());
    }
}
