package com.mangban.location.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.IsolationPointDetailRepository;
import com.mangban.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final IsolationPointDetailRepository isolationPointDetailRepository;
    private final LocationChangeRecordService changeRecordService;

    public List<Location> getTree() {
        List<Location> roots = locationRepository.findByParentIdIsNull();
        roots.forEach(this::loadChildren);
        return roots;
    }

    private void loadChildren(Location location) {
        List<Location> children = locationRepository.findByParentId(location.getId());
        location.setChildren(children);
        children.forEach(this::loadChildren);
    }

    public Location findById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "位置不存在"));
    }

    public Location create(Location location) {
        validateHierarchy(location);
        validateCode(location, null);
        Location saved = locationRepository.save(location);
        if ("ISOLATION_POINT".equals(saved.getType())) {
            IsolationPointDetail detail = new IsolationPointDetail();
            detail.setLocation(saved);
            isolationPointDetailRepository.save(detail);
        }
        changeRecordService.createChangeRecord(saved.getId(), "CREATE", "*", null,
                saved.getName(), null);
        return saved;
    }

    public Location update(Long id, Location location) {
        Location existing = findById(id);
        validateHierarchy(location);
        validateCode(location, existing.getCode());

        List<String> changedFields = new ArrayList<>();
        recordIfChanged(changedFields, id, "name", existing.getName(), location.getName());
        recordIfChanged(changedFields, id, "description", existing.getDescription(), location.getDescription());
        recordIfChanged(changedFields, id, "type", existing.getType(), location.getType());
        recordIfChanged(changedFields, id, "parentId",
                existing.getParentId() != null ? existing.getParentId().toString() : null,
                location.getParentId() != null ? location.getParentId().toString() : null);
        recordIfChanged(changedFields, id, "code", existing.getCode(), location.getCode());

        existing.setName(location.getName());
        existing.setDescription(location.getDescription());
        existing.setType(location.getType());
        existing.setParentId(location.getParentId());
        existing.setCode(location.getCode());
        existing.setLevel(location.getLevel());
        return locationRepository.save(existing);
    }

    public void delete(Long id) {
        List<Location> children = locationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException(400, "存在子节点，无法删除");
        }
        Location existing = findById(id);
        changeRecordService.createChangeRecord(id, "DELETE", "*", existing.getName(), null, null);
        locationRepository.deleteById(id);
    }

    private void recordIfChanged(List<String> changedFields, Long locationId,
                                  String fieldName, String oldValue, String newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            changedFields.add(fieldName);
            changeRecordService.createChangeRecord(locationId, "UPDATE", fieldName,
                    oldValue, newValue, null);
        }
    }

    private void validateHierarchy(Location location) {
        String type = location.getType();
        Long parentId = location.getParentId();

        if ("FACTORY".equals(type)) {
            if (parentId != null) {
                throw new BusinessException(400, "工厂节点必须为顶级节点");
            }
            location.setLevel(0);
            return;
        }

        if (parentId == null) {
            throw new BusinessException(400, "非工厂节点必须指定父节点");
        }

        Location parent = findById(parentId);
        String parentType = parent.getType();

        switch (type) {
            case "EQUIPMENT":
                if (!"FACTORY".equals(parentType)) {
                    throw new BusinessException(400, "装置节点必须挂在工厂节点下");
                }
                break;
            case "UNIT":
                if (!"EQUIPMENT".equals(parentType)) {
                    throw new BusinessException(400, "单元节点必须挂在装置节点下");
                }
                break;
            case "ISOLATION_POINT":
                if (!"UNIT".equals(parentType)) {
                    throw new BusinessException(400, "隔离点节点必须挂在单元节点下");
                }
                break;
            default:
                throw new BusinessException(400, "无效的位置类型: " + type);
        }

        location.setLevel(parent.getLevel() + 1);
    }

    private void validateCode(Location location, String existingCode) {
        String code = location.getCode();

        if ("ISOLATION_POINT".equals(location.getType())) {
            if (code == null || code.isBlank()) {
                throw new BusinessException(400, "隔离点编码不能为空");
            }
        }

        if (code != null && !code.isBlank()) {
            if (!code.equals(existingCode) && locationRepository.existsByCode(code)) {
                throw new BusinessException(400, "位置编码已存在");
            }
        }
    }
}
