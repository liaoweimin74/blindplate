package com.mangban.location.repository;

import com.mangban.location.entity.IsolationPointDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IsolationPointDetailRepository extends JpaRepository<IsolationPointDetail, Long> {

    Optional<IsolationPointDetail> findByLocationId(Long locationId);
}
