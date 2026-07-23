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
                        cb.like(root.get("spec"), pattern),
                        cb.like(root.get("manufacturer"), pattern),
                        cb.like(root.get("factoryCode"), pattern)
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

    private static final String[] TEMPLATE_HEADERS = {
            "code", "modelType", "diameter", "thickness", "pressure",
            "material", "manufacturer", "factoryCode", "purchaseDate"
    };

    public Map<String, Object> importExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "导入文件不能为空");
        }
        List<Map<String, String>> errors = new ArrayList<>();
        int successCount = 0;
        int errorCount = 0;
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount > 5001) {
                throw new BusinessException(400, "单次导入不能超过5000行");
            }
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String code = formatter.formatCellValue(row.getCell(0)).trim();
                    if (code.isEmpty()) {
                        errors.add(Map.of("row", String.valueOf(i + 1), "error", "编号不能为空"));
                        errorCount++;
                        continue;
                    }
                    if (blindPlateRepository.existsByCode(code)) {
                        errors.add(Map.of("row", String.valueOf(i + 1), "code", code, "error", "编号已存在"));
                        errorCount++;
                        continue;
                    }
                    BlindPlate plate = new BlindPlate();
                    plate.setCode(code);
                    plate.setModelType(getStringCell(row.getCell(1)));
                    plate.setDiameter(getIntCell(row.getCell(2)));
                    plate.setThickness(getDoubleCell(row.getCell(3)));
                    plate.setPressure(getDoubleCell(row.getCell(4)));
                    plate.setMaterial(getStringCell(row.getCell(5)));
                    plate.setManufacturer(getStringCell(row.getCell(6)));
                    plate.setFactoryCode(getStringCell(row.getCell(7)));
                    plate.setPurchaseDate(getDateCell(row.getCell(8)));
                    plate.setStatus("in_stock");
                    plate.setQrCode(generateQrCode());
                    plate.setRfidTag(java.util.UUID.randomUUID().toString());
                    plate.setInstallCount(0);
                    plate.setTotalUsageDays(0.0);
                    plate.setLifecycleStatus("normal");
                    blindPlateRepository.save(plate);
                    successCount++;
                } catch (Exception e) {
                    errors.add(Map.of("row", String.valueOf(i + 1), "error", e.getMessage()));
                    errorCount++;
                }
            }
        } catch (IOException e) {
            throw new BusinessException(500, "文件解析失败: " + e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("errorCount", errorCount);
        result.put("errors", errors);
        return result;
    }

    public byte[] exportExcel(String keyword, String modelType, String material,
                              String status, String lifecycleStatus) {
        Specification<BlindPlate> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("code"), pattern),
                        cb.like(root.get("name"), pattern),
                        cb.like(root.get("spec"), pattern),
                        cb.like(root.get("manufacturer"), pattern),
                        cb.like(root.get("factoryCode"), pattern)
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
        List<BlindPlate> plates = blindPlateRepository.findAll(spec);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("BlindPlates");
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Code", "Name", "ModelType", "Spec", "Material",
                    "Diameter", "Pressure", "Thickness", "Manufacturer", "FactoryCode",
                    "Status", "LifecycleStatus", "PurchaseDate", "NextInspectionDate",
                    "InstallCount", "TotalUsageDays", "QRCode", "RFIDTag", "Remark"
            };
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (int i = 0; i < plates.size(); i++) {
                BlindPlate p = plates.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(p.getId() != null ? p.getId() : 0);
                row.createCell(1).setCellValue(p.getCode() != null ? p.getCode() : "");
                row.createCell(2).setCellValue(p.getName() != null ? p.getName() : "");
                row.createCell(3).setCellValue(p.getModelType() != null ? p.getModelType() : "");
                row.createCell(4).setCellValue(p.getSpec() != null ? p.getSpec() : "");
                row.createCell(5).setCellValue(p.getMaterial() != null ? p.getMaterial() : "");
                row.createCell(6).setCellValue(p.getDiameter() != null ? p.getDiameter() : 0);
                row.createCell(7).setCellValue(p.getPressure() != null ? p.getPressure() : 0);
                row.createCell(8).setCellValue(p.getThickness() != null ? p.getThickness() : 0);
                row.createCell(9).setCellValue(p.getManufacturer() != null ? p.getManufacturer() : "");
                row.createCell(10).setCellValue(p.getFactoryCode() != null ? p.getFactoryCode() : "");
                row.createCell(11).setCellValue(p.getStatus() != null ? p.getStatus() : "");
                row.createCell(12).setCellValue(p.getLifecycleStatus() != null ? p.getLifecycleStatus() : "");
                row.createCell(13).setCellValue(p.getPurchaseDate() != null ? p.getPurchaseDate().format(dtf) : "");
                row.createCell(14).setCellValue(p.getNextInspectionDate() != null ? p.getNextInspectionDate().format(dtf) : "");
                row.createCell(15).setCellValue(p.getInstallCount() != null ? p.getInstallCount() : 0);
                row.createCell(16).setCellValue(p.getTotalUsageDays() != null ? p.getTotalUsageDays() : 0);
                row.createCell(17).setCellValue(p.getQrCode() != null ? p.getQrCode() : "");
                row.createCell(18).setCellValue(p.getRfidTag() != null ? p.getRfidTag() : "");
                row.createCell(19).setCellValue(p.getRemark() != null ? p.getRemark() : "");
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "导出失败: " + e.getMessage());
        }
    }

    public byte[] downloadTemplate() {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Template");
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            for (int i = 0; i < TEMPLATE_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(TEMPLATE_HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            // Add a sample row
            Row sampleRow = sheet.createRow(1);
            sampleRow.createCell(0).setCellValue("BP-001");
            sampleRow.createCell(1).setCellValue("8字盲板");
            sampleRow.createCell(2).setCellValue(100);
            sampleRow.createCell(3).setCellValue(10.0);
            sampleRow.createCell(4).setCellValue(1.6);
            sampleRow.createCell(5).setCellValue("20#钢");
            sampleRow.createCell(6).setCellValue("某某制造厂");
            sampleRow.createCell(7).setCellValue("F001");
            sampleRow.createCell(8).setCellValue("2024-01-15");
            workbook.write(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "模板生成失败: " + e.getMessage());
        }
    }

    // ==================== Excel Cell Helpers ====================

    private String getStringCell(Cell cell) {
        if (cell == null) return null;
        DataFormatter formatter = new DataFormatter();
        String val = formatter.formatCellValue(cell).trim();
        return val.isEmpty() ? null : val;
    }

    private Integer getIntCell(Cell cell) {
        if (cell == null) return null;
        try {
            return (int) cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private Double getDoubleCell(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getDateCell(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } catch (Exception e) {
            try {
                String str = new DataFormatter().formatCellValue(cell).trim();
                if (str.isEmpty()) return null;
                return LocalDate.parse(str);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
