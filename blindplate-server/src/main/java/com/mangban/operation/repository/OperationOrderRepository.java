package com.mangban.operation.repository;

import com.mangban.operation.entity.OperationOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationOrderRepository extends JpaRepository<OperationOrder, Long> {
    Optional<OperationOrder> findByOrderNo(String orderNo);
    boolean existsByOrderNo(String orderNo);
}
