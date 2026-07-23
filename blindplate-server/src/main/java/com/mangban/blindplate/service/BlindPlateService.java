package com.mangban.blindplate.service;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateStatusHistory;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.repository.BlindPlateStatusHistoryRepository;
import com.mangban.common.exception.BusinessException;
import com.mangban.location.entity.Location;
import com.mangban.location.repository.LocationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlindPlateService {

    private final BlindPlateRepository blindPlateRepository;
    private final BlindPlateStatusHistoryRepository statusHistoryRepository;
    private final LocationRepository locationRepository;

    private void validateLocation(Long locationId) {
        if (locationId == null) return;
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new BusinessException(404, "位置不存在"));
        if (!"ISOLATION_POINT".equals(location.getType())) {
            throw new BusinessException(400, "盲板只能分配给隔离点位置");
        }
    }

    private String generateQrCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String maxCode = blindPlateRepository.findMaxCodeStartingWith("BP-" + datePart);
        int seq = 1;
        if (maxCode != null) {
            seq = Integer.parseInt(maxCode.substring(maxCode.length() - 6)) + 1;
        }
        return "BP-" + datePart + "-" + String.format("%06d", seq);
    }

    public Page<BlindPlate> findAll(String keyword, String modelType, String material, String status,
                                    String lifecycleStatus, Pageable pageable) {
        Specification<BlindPlate> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("code"), pattern),
                        cb.like(root.get("name"), pattern),
                        cb.like(root.get("spec"), pattern)
                ));
            }
            if (modelType != null && !modelType.isEmpty()) {
                predicates.add(cb.equal(root.get("modelType"), modelType));
            }
            if (material != null && !material.isEmpty()) {
                predicates.add(cb.equal(root.get("material"), material));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (lifecycleStatus != null && !lifecycleStatus.isEmpty()) {
                predicates.add(cb.equal(root.get("lifecycleStatus"), lifecycleStatus));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return blindPlateRepository.findAll(spec, pageable);
    }

    public BlindPlate findById(Long id) {
        return blindPlateRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "盲板不存在"));
    }

    public BlindPlate create(BlindPlate blindPlate) {
        if (blindPlateRepository.existsByCode(blindPlate.getCode())) {
            throw new BusinessException(400, "盲板编号已存在");
        }
        blindPlate.setQrCode(generateQrCode());
        blindPlate.setRfidTag(java.util.UUID.randomUUID().toString());
        if (blindPlate.getInstallCount() == null) blindPlate.setInstallCount(0);
        if (blindPlate.getTotalUsageDays() == null) blindPlate.setTotalUsageDays(0.0);
        if (blindPlate.getLifecycleStatus() == null) blindPlate.setLifecycleStatus("normal");
        validateLocation(blindPlate.getCurrentLocationId());
        return blindPlateRepository.save(blindPlate);
    }

    public BlindPlate update(Long id, BlindPlate blindPlate) {
        BlindPlate existing = findById(id);
        String oldStatus = existing.getStatus();
        String oldLifecycle = existing.getLifecycleStatus();

        validateLocation(blindPlate.getCurrentLocationId());
        existing.setName(blindPlate.getName());
        existing.setSpec(blindPlate.getSpec());
        existing.setModelType(blindPlate.getModelType());
        existing.setMaterial(blindPlate.getMaterial());
        existing.setDiameter(blindPlate.getDiameter());
        existing.setPressure(blindPlate.getPressure());
        existing.setThickness(blindPlate.getThickness());
        existing.setManufacturer(blindPlate.getManufacturer());
        existing.setFactoryCode(blindPlate.getFactoryCode());
        existing.setPurchaseDate(blindPlate.getPurchaseDate());
        existing.setCurrentLocationId(blindPlate.getCurrentLocationId());
        existing.setInstallCount(blindPlate.getInstallCount());
        existing.setTotalUsageDays(blindPlate.getTotalUsageDays());
        existing.setStatus(blindPlate.getStatus());
        existing.setLifecycleStatus(blindPlate.getLifecycleStatus());
        existing.setNextInspectionDate(blindPlate.getNextInspectionDate());
        existing.setRemark(blindPlate.getRemark());

        BlindPlate saved = blindPlateRepository.save(existing);

        // Record status change if status changed
        if (blindPlate.getStatus() != null && !blindPlate.getStatus().equals(oldStatus)) {
            BlindPlateStatusHistory history = new BlindPlateStatusHistory();
            history.setBlindPlateId(id);
            history.setPreviousStatus(oldStatus);
            history.setNewStatus(blindPlate.getStatus());
            history.setOperator("system");
            history.setChangedAt(LocalDateTime.now());
            statusHistoryRepository.save(history);
        }

        // Record lifecycle status change
        if (blindPlate.getLifecycleStatus() != null && !blindPlate.getLifecycleStatus().equals(oldLifecycle)) {
            BlindPlateStatusHistory history = new BlindPlateStatusHistory();
            history.setBlindPlateId(id);
            history.setPreviousStatus(oldLifecycle);
            history.setNewStatus(blindPlate.getLifecycleStatus());
            history.setOperator("system");
            history.setChangedAt(LocalDateTime.now());
            statusHistoryRepository.save(history);
        }

        return saved;
    }

    public void delete(Long id) {
        blindPlateRepository.deleteById(id);
    }

    public List<BlindPlateStatusHistory> getStatusHistory(Long blindPlateId) {
        return statusHistoryRepository.findByBlindPlateIdOrderByChangedAtDesc(blindPlateId);
    }

    public List<BlindPlate> getInspectionAlerts() {
        return blindPlateRepository.findByLifecycleStatusInOrderByNextInspectionDateAsc(
                List.of("inspection_due", "overdue"));
    }

    public void updateLifecycleStatus(Long id, String newLifecycleStatus) {
        BlindPlate plate = findById(id);
        String old = plate.getLifecycleStatus();
        if (!old.equals(newLifecycleStatus)) {
            plate.setLifecycleStatus(newLifecycleStatus);
            blindPlateRepository.save(plate);
            BlindPlateStatusHistory history = new BlindPlateStatusHistory();
            history.setBlindPlateId(id);
            history.setPreviousStatus(old);
            history.setNewStatus(newLifecycleStatus);
            history.setOperator("system");
            history.setChangedAt(LocalDateTime.now());
            statusHistoryRepository.save(history);
        }
    }

    // ==================== Excel Import/Export ====================

    private static final String[] EXPORT_HEADERS = {
            "编号", "名称", "型号", "规格", "材质", "直径(mm)", "压力(MPa)",
            "厚度(mm)", "制造商", "出厂编号", "状态", "生命周期", "备注"
    };

    public byte[] exportExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("盲板清单");
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXPORT_HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(EXPORT_HEADERS[i]);
            }
            List<BlindPlate> plates = blindPlateRepository.findAll();
            int rowIdx = 1;
            for (BlindPlate bp : plates) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(bp.getCode() != null ? bp.getCode() : "");
                row.createCell(1).setCellValue(bp.getName() != null ? bp.getName() : "");
                row.createCell(2).setCellValue(bp.getModelType() != null ? bp.getModelType() : "");
                row.createCell(3).setCellValue(bp.getSpec() != null ? bp.getSpec() : "");
                row.createCell(4).setCellValue(bp.getMaterial() != null ? bp.getMaterial() : "");
                row.createCell(5).setCellValue(bp.getDiameter() != null ? bp.getDiameter() : 0);
                row.createCell(6).setCellValue(bp.getPressure() != null ? bp.getPressure() : 0);
                row.createCell(7).setCellValue(bp.getThickness() != null ? bp.getThickness() : 0);
                row.createCell(8).setCellValue(bp.getManufacturer() != null ? bp.getManufacturer() : "");
                row.createCell(9).setCellValue(bp.getFactoryCode() != null ? bp.getFactoryCode() : "");
                row.createCell(10).setCellValue(bp.getStatus() != null ? bp.getStatus() : "");
                row.createCell(11).setCellValue(bp.getLifecycleStatus() != null ? bp.getLifecycleStatus() : "");
                row.createCell(12).setCellValue(bp.getRemark() != null ? bp.getRemark() : "");
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "导出失败: " + e.getMessage());
        }
    }

    public Map<String, Object> importExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    BlindPlate bp = new BlindPlate();
                    bp.setCode(getCellString(row.getCell(0)));
                    bp.setName(getCellString(row.getCell(1)));
                    bp.setModelType(getCellString(row.getCell(2)));
                    bp.setSpec(getCellString(row.getCell(3)));
                    bp.setMaterial(getCellString(row.getCell(4)));
                    bp.setDiameter((int) getCellNumber(row.getCell(5)));
                    bp.setPressure(getCellNumber(row.getCell(6)));
                    bp.setThickness(getCellNumber(row.getCell(7)));
                    bp.setManufacturer(getCellString(row.getCell(8)));
                    bp.setFactoryCode(getCellString(row.getCell(9)));
                    bp.setStatus(getCellString(row.getCell(10)));
                    bp.setLifecycleStatus(getCellString(row.getCell(11)));
                    bp.setRemark(getCellString(row.getCell(12)));
                    bp.setQrCode(generateQrCode());
                    bp.setRfidTag(java.util.UUID.randomUUID().toString());
                    bp.setInstallCount(0);
                    bp.setTotalUsageDays(0.0);
                    blindPlateRepository.save(bp);
                    successCount++;
                } catch (Exception e) {
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new BusinessException(500, "导入失败: " + e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        return result;
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private double getCellNumber(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        return 0;
    }
}