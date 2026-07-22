package com.mangban.location;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.IsolationPointDetailRepository;
import com.mangban.location.service.IsolationPointDetailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsolationPointDetailServiceTest {

    @Mock
    private IsolationPointDetailRepository detailRepository;

    @InjectMocks
    private IsolationPointDetailService detailService;

    private IsolationPointDetail createExisting() {
        Location loc = new Location();
        loc.setId(1L);
        IsolationPointDetail d = new IsolationPointDetail();
        d.setId(10L);
        d.setLocation(loc);
        d.setHazardLevel("B");
        d.setIsolationType("BLIND_PLATE");
        d.setMedium("H2");
        return d;
    }

    @Test
    void getByLocationId_returnsDetail() {
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(createExisting()));
        IsolationPointDetail result = detailService.getByLocationId(1L);
        assertEquals(10L, result.getId());
        assertEquals("B", result.getHazardLevel());
    }

    @Test
    void getByLocationId_notFound_throws404() {
        when(detailRepository.findByLocationId(99L)).thenReturn(Optional.empty());
        BusinessException ex = assertThrows(BusinessException.class, () -> detailService.getByLocationId(99L));
        assertEquals(404, ex.getCode());
        assertEquals("隔离点详情不存在", ex.getMessage());
    }

    @Test
    void update_rejectsInvalidHazardLevel() {
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(createExisting()));
        IsolationPointDetail patch = new IsolationPointDetail();
        patch.setHazardLevel("X");
        BusinessException ex = assertThrows(BusinessException.class, () -> detailService.update(1L, patch));
        assertEquals(400, ex.getCode());
        assertEquals("危害等级必须为A、B、C或D", ex.getMessage());
    }

    @Test
    void update_validHazardLevel_succeeds() {
        IsolationPointDetail existing = createExisting();
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(existing));
        when(detailRepository.save(any(IsolationPointDetail.class))).thenAnswer(inv -> inv.getArgument(0));

        IsolationPointDetail patch = new IsolationPointDetail();
        patch.setHazardLevel("A");

        IsolationPointDetail result = detailService.update(1L, patch);
        assertEquals("A", result.getHazardLevel());
        verify(detailRepository).save(any(IsolationPointDetail.class));
    }

    @Test
    void update_rejectsInvalidIsolationType() {
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(createExisting()));
        IsolationPointDetail patch = new IsolationPointDetail();
        patch.setIsolationType("INVALID");
        BusinessException ex = assertThrows(BusinessException.class, () -> detailService.update(1L, patch));
        assertEquals(400, ex.getCode());
        assertEquals("隔离类型无效", ex.getMessage());
    }

    @Test
    void update_rejectsNaNCoord() {
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(createExisting()));
        IsolationPointDetail patch = new IsolationPointDetail();
        patch.setCoordX(Double.NaN);
        BusinessException ex = assertThrows(BusinessException.class, () -> detailService.update(1L, patch));
        assertEquals(400, ex.getCode());
        assertEquals("坐标值无效", ex.getMessage());
    }

    @Test
    void update_validCoord_succeeds() {
        IsolationPointDetail existing = createExisting();
        when(detailRepository.findByLocationId(1L)).thenReturn(Optional.of(existing));
        when(detailRepository.save(any(IsolationPointDetail.class))).thenAnswer(inv -> inv.getArgument(0));

        IsolationPointDetail patch = new IsolationPointDetail();
        patch.setCoordX(10.5);
        patch.setCoordY(20.3);
        patch.setCoordZ(5.0);

        IsolationPointDetail result = detailService.update(1L, patch);
        assertEquals(10.5, result.getCoordX());
        assertEquals(20.3, result.getCoordY());
        assertEquals(5.0, result.getCoordZ());
    }
}
