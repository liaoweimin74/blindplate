package com.mangban.operation.repository;

import com.mangban.operation.entity.OperationOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OperationOrderRepository extends JpaRepository<OperationOrder, Long> {
    Optional<OperationOrder> findByOrderNo(String orderNo);
    boolean existsByOrderNo(String orderNo);

    List<OperationOrder> findByLocationIdAndStatusAndTypeInOrderByActualDateDesc(
            Long locationId, String status, List<String> types);

    List<OperationOrder> findByLocationIdInAndStatusAndTypeInOrderByActualDateDesc(
            List<Long> locationIds, String status, List<String> types);

    List<OperationOrder> findByLocationIdAndStatusInOrderByActualDateDesc(
            Long locationId, List<String> statuses);
}
