package com.mangban.report.service;

import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.operation.repository.OperationOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BlindPlateRepository blindPlateRepository;
    private final OperationOrderRepository orderRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBlindPlates", blindPlateRepository.count());
        stats.put("totalOrders", orderRepository.count());
        return stats;
    }
}
