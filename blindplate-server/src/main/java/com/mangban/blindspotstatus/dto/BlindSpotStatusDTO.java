package com.mangban.blindspotstatus.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlindSpotStatusDTO {
    private Long locationId;
    private String locationName;
    private String locationType;
    private String parentPath;
    private String currentStatus;
    private Long currentBlindPlateId;
    private String currentBlindPlateCode;
    private String currentBlindPlateModel;
    private Boolean removable;
    private Long relatedOperationOrderId;
    private String relatedOrderNo;
    private LocalDateTime lastOperationTime;
    private Double statusDurationHours;
    private Boolean abnormal;
    private String abnormalDescription;
}
