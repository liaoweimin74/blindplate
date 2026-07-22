package com.mangban.blindspotstatus.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StatusHistoryDTO {
    private Long operationOrderId;
    private String orderNo;
    private String operationType;
    private Long blindPlateId;
    private String blindPlateCode;
    private String blindPlateModel;
    private LocalDateTime operationTime;
    private String resultingStatus;
    private String operator;
}
