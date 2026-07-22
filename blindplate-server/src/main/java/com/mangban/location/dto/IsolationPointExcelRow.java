package com.mangban.location.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class IsolationPointExcelRow {

    @ExcelProperty(value = "编码", index = 0)
    private String code;

    @ExcelProperty(value = "名称", index = 1)
    private String name;

    @ExcelProperty(value = "父节点编码", index = 2)
    private String parentCode;

    @ExcelProperty(value = "介质", index = 3)
    private String medium;

    @ExcelProperty(value = "危害等级", index = 4)
    private String hazardLevel;

    @ExcelProperty(value = "隔离类型", index = 5)
    private String isolationType;

    @ExcelProperty(value = "压力", index = 6)
    private String pressure;

    @ExcelProperty(value = "温度", index = 7)
    private String temperature;
}
