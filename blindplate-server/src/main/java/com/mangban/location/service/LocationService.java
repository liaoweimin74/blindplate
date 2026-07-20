package com.mangban.location.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

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
        return locationRepository.save(location);
    }

    public Location update(Long id, Location location) {
        Location existing = findById(id);
        existing.setName(location.getName());
        existing.setDescription(location.getDescription());
        existing.setType(location.getType());
        existing.setParentId(location.getParentId());
        return locationRepository.save(existing);
    }

    public void delete(Long id) {
        List<Location> children = locationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new BusinessException(400, "存在子节点，无法删除");
        }
        locationRepository.deleteById(id);
    }
}
