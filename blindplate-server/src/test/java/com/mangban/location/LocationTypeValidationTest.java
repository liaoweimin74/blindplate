package com.mangban.location;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.IsolationPointDetailRepository;
import com.mangban.location.repository.LocationRepository;
import com.mangban.location.service.LocationChangeRecordService;
import com.mangban.location.service.LocationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationTypeValidationTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private IsolationPointDetailRepository isolationPointDetailRepository;

    @Mock
    private LocationChangeRecordService changeRecordService;

    @InjectMocks
    private LocationService locationService;

    private void stubChangeRecord() {
        doReturn(null).when(changeRecordService).createChangeRecord(
                any(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    void createEquipment_withoutFactoryParent_throws400() {
        Location parent = new Location();
        parent.setId(2L);
        parent.setType("UNIT");
        parent.setLevel(2);

        Location location = new Location();
        location.setType("EQUIPMENT");
        location.setParentId(2L);
        location.setName("Test Equipment");

        when(locationRepository.findById(2L)).thenReturn(Optional.of(parent));

        BusinessException ex = assertThrows(BusinessException.class, () -> locationService.create(location));
        assertEquals(400, ex.getCode());
        assertEquals("装置节点必须挂在工厂节点下", ex.getMessage());
    }

    @Test
    void createIsolationPoint_withUnitParent_succeeds() {
        Location parent = new Location();
        parent.setId(3L);
        parent.setType("UNIT");
        parent.setLevel(2);

        Location location = new Location();
        location.setType("ISOLATION_POINT");
        location.setParentId(3L);
        location.setName("IP-001");
        location.setCode("IP-001");

        when(locationRepository.findById(3L)).thenReturn(Optional.of(parent));
        when(locationRepository.existsByCode("IP-001")).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        stubChangeRecord();

        Location saved = locationService.create(location);

        assertEquals(3, saved.getLevel());
        assertEquals("ISOLATION_POINT", saved.getType());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void createIsolationPoint_withoutCode_throws400() {
        Location parent = new Location();
        parent.setId(3L);
        parent.setType("UNIT");
        parent.setLevel(2);

        Location location = new Location();
        location.setType("ISOLATION_POINT");
        location.setParentId(3L);
        location.setName("No Code IP");
        location.setCode(null);

        when(locationRepository.findById(3L)).thenReturn(Optional.of(parent));

        BusinessException ex = assertThrows(BusinessException.class, () -> locationService.create(location));
        assertEquals(400, ex.getCode());
        assertEquals("隔离点编码不能为空", ex.getMessage());
    }

    @Test
    void createFactory_withNullParent_succeeds() {
        Location location = new Location();
        location.setType("FACTORY");
        location.setParentId(null);
        location.setName("Test Factory");

        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        stubChangeRecord();

        Location saved = locationService.create(location);

        assertEquals(0, saved.getLevel());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void createFactory_withParent_throws400() {
        Location location = new Location();
        location.setType("FACTORY");
        location.setParentId(1L);
        location.setName("Factory with parent");

        BusinessException ex = assertThrows(BusinessException.class, () -> locationService.create(location));
        assertEquals(400, ex.getCode());
        assertEquals("工厂节点必须为顶级节点", ex.getMessage());
    }

    @Test
    void create_duplicateCode_throws400() {
        Location parent = new Location();
        parent.setId(3L);
        parent.setType("UNIT");
        parent.setLevel(2);

        Location location = new Location();
        location.setType("ISOLATION_POINT");
        location.setParentId(3L);
        location.setName("Duplicate Code IP");
        location.setCode("IP-001");

        when(locationRepository.findById(3L)).thenReturn(Optional.of(parent));
        when(locationRepository.existsByCode("IP-001")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> locationService.create(location));
        assertEquals(400, ex.getCode());
        assertEquals("位置编码已存在", ex.getMessage());
    }

    @Test
    void createUnit_withoutEquipmentParent_throws400() {
        Location parent = new Location();
        parent.setId(1L);
        parent.setType("FACTORY");
        parent.setLevel(0);

        Location location = new Location();
        location.setType("UNIT");
        location.setParentId(1L);
        location.setName("Bad Unit");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(parent));

        BusinessException ex = assertThrows(BusinessException.class, () -> locationService.create(location));
        assertEquals(400, ex.getCode());
        assertEquals("单元节点必须挂在装置节点下", ex.getMessage());
    }

    @Test
    void createEquipment_withFactoryParent_succeeds() {
        Location parent = new Location();
        parent.setId(1L);
        parent.setType("FACTORY");
        parent.setLevel(0);

        Location location = new Location();
        location.setType("EQUIPMENT");
        location.setParentId(1L);
        location.setName("Catalytic Cracker");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(locationRepository.save(any(Location.class))).thenAnswer(inv -> inv.getArgument(0));
        stubChangeRecord();

        Location saved = locationService.create(location);

        assertEquals(1, saved.getLevel());
        verify(locationRepository).save(any(Location.class));
    }
}
