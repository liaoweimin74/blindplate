package com.mangban.blindspotstatus.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindspotstatus.dto.BlindSpotStatusDTO;
import com.mangban.blindspotstatus.dto.StatusHistoryDTO;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.LocationRepository;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.repository.OperationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlindSpotStatusService {

    private static final long ABNORMAL_BLIND_DURATION_HOURS = 720;
    private static final String STATUS_COMPLETED = "completed";
    private static final String TYPE_INSTALL = "INSTALL";
    private static final String TYPE_REMOVE = "REMOVE";
    private static final String TYPE_INSPECT = "INSPECT";
    private static final List<String> STATUS_CHANGE_TYPES = List.of(TYPE_INSTALL, TYPE_REMOVE);
    private static final List<String> ALL_OPERATION_TYPES = List.of(TYPE_INSTALL, TYPE_REMOVE, TYPE_INSPECT);

    private final LocationRepository locationRepository;
    private final OperationOrderRepository operationOrderRepository;
    private final BlindPlateRepository blindPlateRepository;

    public List<BlindSpotStatusDTO> getStatusList(Long locationId, String status, Boolean abnormalOnly) {
        List<Location> targetLocations = getTargetLocations(locationId);
        if (targetLocations.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> locationIds = targetLocations.stream()
                .map(Location::getId)
                .collect(Collectors.toList());

        List<OperationOrder> operations = operationOrderRepository
                .findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
                        locationIds, STATUS_COMPLETED, STATUS_CHANGE_TYPES);

        Map<Long, List<OperationOrder>> operationsByLocation = operations.stream()
                .collect(Collectors.groupingBy(OperationOrder::getLocationId));

        Set<Long> blindPlateIds = operations.stream()
                .map(OperationOrder::getBlindplateId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, BlindPlate> blindPlateMap = blindPlateIds.isEmpty()
                ? Collections.emptyMap()
                : blindPlateRepository.findAllById(blindPlateIds).stream()
                        .collect(Collectors.toMap(BlindPlate::getId, bp -> bp));

        Map<Long, String> parentPathMap = buildParentPathMap(targetLocations);

        List<BlindSpotStatusDTO> result = new ArrayList<>();
        for (Location loc : targetLocations) {
            List<OperationOrder> locOps = operationsByLocation.getOrDefault(loc.getId(), Collections.emptyList());
            BlindSpotStatusDTO dto = buildDTO(loc, locOps, blindPlateMap, parentPathMap.get(loc.getId()));
            result.add(dto);
        }

        if (status != null && !status.isEmpty()) {
            result = result.stream()
                    .filter(dto -> status.equals(dto.getCurrentStatus()))
                    .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(abnormalOnly)) {
            result = result.stream()
                    .filter(BlindSpotStatusDTO::getAbnormal)
                    .collect(Collectors.toList());
        }

        return result;
    }

    public List<StatusHistoryDTO> getHistory(Long locationId) {
        List<OperationOrder> operations = operationOrderRepository
                .findByLocationIdAndStatusInOrderByActualDateDesc(locationId, List.of(STATUS_COMPLETED));

        Set<Long> blindPlateIds = operations.stream()
                .map(OperationOrder::getBlindplateId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, BlindPlate> blindPlateMap = blindPlateIds.isEmpty()
                ? Collections.emptyMap()
                : blindPlateRepository.findAllById(blindPlateIds).stream()
                        .collect(Collectors.toMap(BlindPlate::getId, bp -> bp));

        List<StatusHistoryDTO> result = new ArrayList<>();
        String previousStatus = "未知";
        List<OperationOrder> chronological = new ArrayList<>(operations);
        Collections.reverse(chronological);
        Map<Long, String> statusAfterOperation = new LinkedHashMap<>();
        for (OperationOrder op : chronological) {
            if (TYPE_INSTALL.equals(op.getType())) {
                previousStatus = "盲";
            } else if (TYPE_REMOVE.equals(op.getType())) {
                previousStatus = "通";
            }
            statusAfterOperation.put(op.getId(), previousStatus);
        }

        for (OperationOrder op : operations) {
            StatusHistoryDTO dto = new StatusHistoryDTO();
            dto.setOperationOrderId(op.getId());
            dto.setOrderNo(op.getOrderNo());
            dto.setOperationType(op.getType());
            dto.setBlindPlateId(op.getBlindplateId());
            dto.setOperationTime(op.getActualDate());
            dto.setResultingStatus(statusAfterOperation.getOrDefault(op.getId(), "未知"));

            if (op.getBlindplateId() != null) {
                BlindPlate bp = blindPlateMap.get(op.getBlindplateId());
                if (bp != null) {
                    dto.setBlindPlateCode(bp.getCode());
                    dto.setBlindPlateModel(bp.getSpec());
                }
            }
            result.add(dto);
        }
        return result;
    }

    private BlindSpotStatusDTO buildDTO(Location loc, List<OperationOrder> locOps,
                                        Map<Long, BlindPlate> blindPlateMap, String parentPath) {
        BlindSpotStatusDTO dto = new BlindSpotStatusDTO();
        dto.setLocationId(loc.getId());
        dto.setLocationName(loc.getName());
        dto.setLocationType(loc.getType());
        dto.setParentPath(parentPath != null ? parentPath : "");
        dto.setAbnormal(false);
        dto.setRemovable(false);

        if (locOps.isEmpty()) {
            dto.setCurrentStatus("未知");
            return dto;
        }

        OperationOrder latest = locOps.get(0);
        dto.setRelatedOperationOrderId(latest.getId());
        dto.setRelatedOrderNo(latest.getOrderNo());
        dto.setLastOperationTime(latest.getActualDate());

        if (latest.getActualDate() != null) {
            double durationHours = Duration.between(latest.getActualDate(), LocalDateTime.now()).toHours();
            dto.setStatusDurationHours(durationHours);
        }

        if (TYPE_INSTALL.equals(latest.getType())) {
            dto.setCurrentStatus("盲");
            dto.setCurrentBlindPlateId(latest.getBlindplateId());
            if (latest.getBlindplateId() != null) {
                BlindPlate bp = blindPlateMap.get(latest.getBlindplateId());
                if (bp != null) {
                    dto.setCurrentBlindPlateCode(bp.getCode());
                    dto.setCurrentBlindPlateModel(bp.getSpec());
                }
            }
            if (dto.getStatusDurationHours() != null
                    && dto.getStatusDurationHours() > ABNORMAL_BLIND_DURATION_HOURS) {
                dto.setAbnormal(true);
                dto.setAbnormalDescription("盲板已挂载超过30天未拆除");
            }
        } else if (TYPE_REMOVE.equals(latest.getType())) {
            dto.setCurrentStatus("通");
            dto.setCurrentBlindPlateId(latest.getBlindplateId());
            if (latest.getBlindplateId() != null) {
                BlindPlate bp = blindPlateMap.get(latest.getBlindplateId());
                if (bp != null) {
                    dto.setCurrentBlindPlateCode(bp.getCode());
                    dto.setCurrentBlindPlateModel(bp.getSpec());
                    dto.setRemovable(true);
                }
            }
        } else {
            dto.setCurrentStatus("未知");
        }

        if (!locOps.isEmpty()) {
            long installCount = locOps.stream().filter(o -> TYPE_INSTALL.equals(o.getType())).count();
            long removeCount = locOps.stream().filter(o -> TYPE_REMOVE.equals(o.getType())).count();
            if (installCount > removeCount + 1) {
                dto.setAbnormal(true);
                if (dto.getAbnormalDescription() == null) {
                    dto.setAbnormalDescription("存在连续安装操作无拆除记录，状态冲突");
                }
            }
        }

        return dto;
    }

    private List<Location> getTargetLocations(Long locationId) {
        if (locationId == null) {
            return locationRepository.findAll();
        }
        List<Location> result = new ArrayList<>();
        Location root = locationRepository.findById(locationId).orElse(null);
        if (root == null) {
            return result;
        }
        result.add(root);
        collectChildren(root.getId(), result);
        return result;
    }

    private void collectChildren(Long parentId, List<Location> accumulator) {
        List<Location> children = locationRepository.findByParentId(parentId);
        for (Location child : children) {
            accumulator.add(child);
            collectChildren(child.getId(), accumulator);
        }
    }

    private Map<Long, String> buildParentPathMap(List<Location> locations) {
        Map<Long, Location> locationById = locations.stream()
                .collect(Collectors.toMap(Location::getId, loc -> loc));
        Map<Long, String> pathMap = new HashMap<>();
        for (Location loc : locations) {
            StringBuilder path = new StringBuilder();
            Long currentParentId = loc.getParentId();
            Set<Long> visited = new HashSet<>();
            visited.add(loc.getId());
            List<String> ancestors = new ArrayList<>();
            while (currentParentId != null && !visited.contains(currentParentId)) {
                visited.add(currentParentId);
                Location parent = locationById.get(currentParentId);
                if (parent == null) {
                    parent = locationRepository.findById(currentParentId).orElse(null);
                }
                if (parent == null) break;
                ancestors.add(0, parent.getName());
                currentParentId = parent.getParentId();
            }
            if (!ancestors.isEmpty()) {
                path.append(String.join(" > ", ancestors));
            }
            pathMap.put(loc.getId(), path.toString());
        }
        return pathMap;
    }
}
