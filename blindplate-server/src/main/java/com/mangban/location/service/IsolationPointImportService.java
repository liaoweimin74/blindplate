package com.mangban.location.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.mangban.common.exception.BusinessException;
import com.mangban.location.dto.IsolationPointExcelRow;
import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.IsolationPointDetailRepository;
import com.mangban.location.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IsolationPointImportService {

    private final LocationRepository locationRepository;
    private final IsolationPointDetailRepository detailRepository;
    private final LocationChangeRecordService changeRecordService;

    public List<String> importFromExcel(MultipartFile file, Long applicantId) {
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }

        List<String> errors = new ArrayList<>();
        List<Location> toSave = new ArrayList<>();
        List<IsolationPointDetail> detailsToSave = new ArrayList<>();

        try {
            EasyExcel.read(file.getInputStream(), IsolationPointExcelRow.class,
                    new PageReadListener<IsolationPointExcelRow>(dataList -> {
                        for (IsolationPointExcelRow row : dataList) {
                            try {
                                validateRow(row);
                                Location parent = findParent(row.getParentCode());

                                Location loc = new Location();
                                loc.setCode(row.getCode());
                                loc.setName(row.getName());
                                loc.setType("ISOLATION_POINT");
                                loc.setParentId(parent.getId());
                                loc.setLevel(parent.getLevel() + 1);

                                toSave.add(loc);

                                IsolationPointDetail detail = new IsolationPointDetail();
                                detail.setMedium(row.getMedium());
                                detail.setHazardLevel(row.getHazardLevel());
                                detail.setIsolationType(row.getIsolationType());
                                if (row.getPressure() != null) {
                                    detail.setPressure(Double.parseDouble(row.getPressure()));
                                }
                                if (row.getTemperature() != null) {
                                    detail.setTemperature(Double.parseDouble(row.getTemperature()));
                                }
                                detailsToSave.add(detail);
                            } catch (BusinessException e) {
                                errors.add("行" + row.getCode() + ": " + e.getMessage());
                            } catch (NumberFormatException e) {
                                errors.add("行" + row.getCode() + ": 数值格式错误");
                            }
                        }
                    })).sheet().doRead();
        } catch (IOException e) {
            throw new BusinessException(400, "文件读取失败: " + e.getMessage());
        }

        for (int i = 0; i < toSave.size(); i++) {
            Location saved = locationRepository.save(toSave.get(i));
            IsolationPointDetail detail = detailsToSave.get(i);
            detail.setLocation(saved);
            detailRepository.save(detail);
            changeRecordService.createChangeRecord(saved.getId(), "CREATE", "*",
                    null, saved.getName(), applicantId);
        }

        return errors;
    }

    private void validateRow(IsolationPointExcelRow row) {
        if (row.getCode() == null || row.getCode().isBlank()) {
            throw new BusinessException(400, "编码不能为空");
        }
        if (row.getName() == null || row.getName().isBlank()) {
            throw new BusinessException(400, "名称不能为空");
        }
        if (row.getHazardLevel() != null && !List.of("A", "B", "C", "D").contains(row.getHazardLevel())) {
            throw new BusinessException(400, "危害等级必须为A、B、C或D");
        }
        if (locationRepository.existsByCode(row.getCode())) {
            throw new BusinessException(400, "编码已存在");
        }
    }

    private Location findParent(String parentCode) {
        if (parentCode == null || parentCode.isBlank()) {
            throw new BusinessException(400, "父节点编码不能为空");
        }
        return locationRepository.findByCode(parentCode)
                .orElseThrow(() -> new BusinessException(404, "父节点不存在: " + parentCode));
    }
}
