package com.mangban.blindspotstatus.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.LocationRepository;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.repository.OperationOrderRepository;
import com.mangban.blindspotstatus.dto.BlindSpotStatusDTO;
import com.mangban.blindspotstatus.dto.StatusHistoryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlindSpotStatusServiceTest {

    @Mock
    private LocationRepository locationRepository;
    @Mock
    private OperationOrderRepository operationOrderRepository;
    @Mock
    private BlindPlateRepository blindPlateRepository;

    @InjectMocks
    private BlindSpotStatusService service;

    private Location loc1;
    private Location loc2;
    private Location loc3;

    @BeforeEach
    void setUp() {
        loc1 = new Location();
        loc1.setId(1L);
        loc1.setName("隔离点A");
        loc1.setType("隔离点");
        loc1.setParentId(10L);

        loc2 = new Location();
        loc2.setId(2L);
        loc2.setName("隔离点B");
        loc2.setType("隔离点");
        loc2.setParentId(10L);

        loc3 = new Location();
        loc3.setId(3L);
        loc3.setName("隔离点C");
        loc3.setType("隔离点");
        loc3.setParentId(10L);
    }

    @Test
    void getStatusList_noFilters_returnsAllLocationsWithComputedStatus() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2, loc3));
        when(locationRepository.findById(10L)).thenReturn(Optional.empty());
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(dto -> "未知".equals(dto.getCurrentStatus())));
    }

    @Test
    void getStatusList_latestInstall_statusBlind() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder installOp = createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                LocalDateTime.now().minusDays(5), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(installOp));

        BlindPlate bp = new BlindPlate();
        bp.setId(1L);
        bp.setCode("BP-001");
        bp.setSpec("8字盲板DN100");
        when(blindPlateRepository.findAllById(anySet())).thenReturn(List.of(bp));

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(1, result.size());
        assertEquals("盲", result.get(0).getCurrentStatus());
        assertEquals("BP-001", result.get(0).getCurrentBlindPlateCode());
        assertFalse(result.get(0).getAbnormal());
    }

    @Test
    void getStatusList_latestRemove_statusPass() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder removeOp = createOp(101L, "ORD-002", "REMOVE", 1L, 1L,
                LocalDateTime.now().minusDays(2), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(removeOp));

        BlindPlate bp = new BlindPlate();
        bp.setId(1L);
        bp.setCode("BP-001");
        bp.setSpec("8字盲板DN100");
        when(blindPlateRepository.findAllById(anySet())).thenReturn(List.of(bp));

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(1, result.size());
        assertEquals("通", result.get(0).getCurrentStatus());
        assertTrue(result.get(0).getRemovable());
    }

    @Test
    void getStatusList_noOperations_statusUnknown() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(1, result.size());
        assertEquals("未知", result.get(0).getCurrentStatus());
        assertFalse(result.get(0).getAbnormal());
    }

    @Test
    void getStatusList_blindOver720Hours_abnormal() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder installOp = createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                LocalDateTime.now().minusDays(35), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(installOp));

        when(blindPlateRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAbnormal());
        assertEquals("盲板已挂载超过30天未拆除", result.get(0).getAbnormalDescription());
    }

    @Test
    void getStatusList_consecutiveInstallsNoRemove_abnormalConflict() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder install1 = createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                LocalDateTime.now().minusDays(10), "completed");
        OperationOrder install2 = createOp(101L, "ORD-002", "INSTALL", 2L, 1L,
                LocalDateTime.now().minusDays(5), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(install2, install1));

        when(blindPlateRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, null);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAbnormal());
        assertTrue(result.get(0).getAbnormalDescription().contains("状态冲突"));
    }

    @Test
    void getStatusList_filterByStatus() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder installOp = createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                LocalDateTime.now().minusDays(5), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(installOp));
        when(blindPlateRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, "盲", null);

        assertEquals(1, result.size());
        assertEquals("盲", result.get(0).getCurrentStatus());
    }

    @Test
    void getStatusList_filterAbnormalOnly() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2));
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());

        OperationOrder longInstall = createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                LocalDateTime.now().minusDays(40), "completed");
        OperationOrder normalInstall = createOp(101L, "ORD-002", "INSTALL", 2L, 2L,
                LocalDateTime.now().minusDays(5), "completed");
        when(operationOrderRepository.findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                anyList(), eq("completed"), anyList()))
                .thenReturn(List.of(longInstall, normalInstall));
        when(blindPlateRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        List<BlindSpotStatusDTO> result = service.getStatusList(null, null, true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getAbnormal());
    }

    @Test
    void getHistory_returnsOperationsOrderedDescWithResultingStatus() {
        when(operationOrderRepository.findByLocationIdAndStatusInOrderByActualDateDesc(
                eq(1L), anyList()))
                .thenReturn(List.of(
                        createOp(101L, "ORD-002", "REMOVE", 1L, 1L,
                                LocalDateTime.now().minusDays(2), "completed"),
                        createOp(100L, "ORD-001", "INSTALL", 1L, 1L,
                                LocalDateTime.now().minusDays(10), "completed")
                ));
        when(blindPlateRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        List<StatusHistoryDTO> result = service.getHistory(1L);

        assertEquals(2, result.size());
        assertEquals("通", result.get(0).getResultingStatus());
        assertEquals("盲", result.get(1).getResultingStatus());
    }

    @Test
    void getHistory_noOperations_returnsEmpty() {
        when(operationOrderRepository.findByLocationIdAndStatusInOrderByActualDateDesc(
                eq(1L), anyList()))
                .thenReturn(Collections.emptyList());

        List<StatusHistoryDTO> result = service.getHistory(1L);

        assertTrue(result.isEmpty());
    }

    private OperationOrder createOp(Long id, String orderNo, String type, Long blindplateId,
                                    Long locationId, LocalDateTime actualDate, String status) {
        OperationOrder op = new OperationOrder();
        op.setId(id);
        op.setOrderNo(orderNo);
        op.setType(type);
        op.setBlindplateId(blindplateId);
        op.setLocationId(locationId);
        op.setActualDate(actualDate);
        op.setStatus(status);
        return op;
    }
}
