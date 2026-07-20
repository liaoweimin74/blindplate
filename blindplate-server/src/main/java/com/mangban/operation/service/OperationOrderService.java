package com.mangban.operation.service;

import com.mangban.common.exception.BusinessException;
import com.mangban.operation.entity.OperationOrder;
import com.mangban.operation.repository.OperationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationOrderService {

    private final OperationOrderRepository orderRepository;

    public List<OperationOrder> findAll() {
        return orderRepository.findAll();
    }

    public OperationOrder findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "作业工单不存在"));
    }

    public OperationOrder create(OperationOrder order) {
        if (orderRepository.existsByOrderNo(order.getOrderNo())) {
            throw new BusinessException(400, "工单编号已存在");
        }
        return orderRepository.save(order);
    }

    public OperationOrder update(Long id, OperationOrder order) {
        OperationOrder existing = findById(id);
        existing.setType(order.getType());
        existing.setBlindplateId(order.getBlindplateId());
        existing.setLocationId(order.getLocationId());
        existing.setStatus(order.getStatus());
        existing.setPlannedDate(order.getPlannedDate());
        existing.setActualDate(order.getActualDate());
        existing.setRemark(order.getRemark());
        return orderRepository.save(existing);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}
