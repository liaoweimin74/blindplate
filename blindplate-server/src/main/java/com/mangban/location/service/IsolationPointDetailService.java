package com.mangban.location.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.repository.IsolationPointDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class IsolationPointDetailService {

    private static final Set<String> VALID_HAZARD_LEVELS = Set.of("A", "B", "C", "D");
    private static final Set<String> VALID_ISOLATION_TYPES = Set.of("BLIND_PLATE", "DOUBLE_BLOCK", "VALVE", "OTHER");

    private final IsolationPointDetailRepository detailRepository;

    public IsolationPointDetail getByLocationId(Long locationId) {
        return detailRepository.findByLocationId(locationId)
                .orElseThrow(() -> new BusinessException(404, "隔离点详情不存在"));
    }

    public IsolationPointDetail update(Long locationId, IsolationPointDetail patch) {
        IsolationPointDetail existing = getByLocationId(locationId);

        if (patch.getPidDiagramRef() != null) {
            existing.setPidDiagramRef(patch.getPidDiagramRef());
        }
        if (patch.getMedium() != null) {
            existing.setMedium(patch.getMedium());
        }
        if (patch.getPressure() != null) {
            existing.setPressure(patch.getPressure());
        }
        if (patch.getTemperature() != null) {
            existing.setTemperature(patch.getTemperature());
        }
        if (patch.getHazardLevel() != null) {
            if (!VALID_HAZARD_LEVELS.contains(patch.getHazardLevel())) {
                throw new BusinessException(400, "危害等级必须为A、B、C或D");
            }
            existing.setHazardLevel(patch.getHazardLevel());
        }
        if (patch.getIsolationType() != null) {
            if (!VALID_ISOLATION_TYPES.contains(patch.getIsolationType())) {
                throw new BusinessException(400, "隔离类型无效");
            }
            existing.setIsolationType(patch.getIsolationType());
        }
        if (patch.getCoordX() != null) {
            if (!Double.isFinite(patch.getCoordX())) {
                throw new BusinessException(400, "坐标值无效");
            }
            existing.setCoordX(patch.getCoordX());
        }
        if (patch.getCoordY() != null) {
            if (!Double.isFinite(patch.getCoordY())) {
                throw new BusinessException(400, "坐标值无效");
            }
            existing.setCoordY(patch.getCoordY());
        }
        if (patch.getCoordZ() != null) {
            if (!Double.isFinite(patch.getCoordZ())) {
                throw new BusinessException(400, "坐标值无效");
            }
            existing.setCoordZ(patch.getCoordZ());
        }
        if (patch.getDiagramId() != null) {
            existing.setDiagramId(patch.getDiagramId());
        }

        return detailRepository.save(existing);
    }
}
