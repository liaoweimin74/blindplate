package com.mangban.location.repository;

import com.mangban.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByParentId(Long parentId);
    List<Location> findByParentIdIsNull();
}
