package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlindPlateService {

    private final BlindPlateRepository blindPlateRepository;

    public List<BlindPlate> findAll() {
        return blindPlateRepository.findAll();
    }

    public BlindPlate findById(Long id) {
        return blindPlateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "盲板不存在"));
    }

    public BlindPlate create(BlindPlate blindPlate) {
        if (blindPlateRepository.existsByCode(blindPlate.getCode())) {
            throw new BusinessException(400, "盲板编号已存在");
        }
        return blindPlateRepository.save(blindPlate);
    }

    public BlindPlate update(Long id, BlindPlate blindPlate) {
        BlindPlate existing = findById(id);
        existing.setName(blindPlate.getName());
        existing.setSpec(blindPlate.getSpec());
        existing.setMaterial(blindPlate.getMaterial());
        existing.setDiameter(blindPlate.getDiameter());
        existing.setPressure(blindPlate.getPressure());
        existing.setManufacturer(blindPlate.getManufacturer());
        existing.setStatus(blindPlate.getStatus());
        existing.setRemark(blindPlate.getRemark());
        return blindPlateRepository.save(existing);
    }

    public void delete(Long id) {
        blindPlateRepository.deleteById(id);
    }
}
