package com.mangban.blindplate;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateStatusHistory;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.repository.BlindPlateStatusHistoryRepository;
import com.mangban.blindplate.service.BlindPlateService;
import com.mangban.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BlindPlateServiceTest {

    @Autowired
    private BlindPlateService blindPlateService;

    @Autowired
    private BlindPlateRepository blindPlateRepository;

    @Autowired
    private BlindPlateStatusHistoryRepository statusHistoryRepository;

    private BlindPlate createTestPlate(String code) {
        BlindPlate plate = new BlindPlate();
        plate.setCode(code);
        plate.setName("Test Plate");
        plate.setMaterial("20#钢");
        plate.setStatus("in_stock");
        return plate;
    }

    @BeforeEach
    void cleanUp() {
        statusHistoryRepository.deleteAll();
        blindPlateRepository.deleteAll();
    }

    // ==================== QR Code Generation ====================

    @Test
    @DisplayName("create() should auto-generate QR code in format BP-{yyyyMMdd}-{6-digit sequence}")
    void testQrCodeGenerationFormat() {
        BlindPlate plate = createTestPlate("BP-TEST-001");
        BlindPlate saved = blindPlateService.create(plate);

        assertNotNull(saved.getQrCode());
        String expectedPrefix = "BP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        assertTrue(saved.getQrCode().startsWith(expectedPrefix),
                "QR code should start with " + expectedPrefix + " but was " + saved.getQrCode());
        // Should end with 6-digit sequence
        String seqPart = saved.getQrCode().substring(saved.getQrCode().length() - 6);
        assertDoesNotThrow(() -> Integer.parseInt(seqPart),
                "QR code sequence part should be numeric: " + seqPart);
        assertEquals(6, seqPart.length());
    }

    @Test
    @DisplayName("create() should auto-generate RFID tag as UUID")
    void testRfidTagGeneration() {
        BlindPlate plate = createTestPlate("BP-TEST-002");
        BlindPlate saved = blindPlateService.create(plate);

        assertNotNull(saved.getRfidTag());
        assertDoesNotThrow(() -> java.util.UUID.fromString(saved.getRfidTag()),
                "RFID tag should be a valid UUID: " + saved.getRfidTag());
    }

    @Test
    @DisplayName("create() should increment QR code sequence for same-day entries")
    void testQrCodeSequenceIncrement() {
        BlindPlate plate1 = createTestPlate("BP-SEQ-001");
        BlindPlate plate2 = createTestPlate("BP-SEQ-002");

        BlindPlate saved1 = blindPlateService.create(plate1);
        BlindPlate saved2 = blindPlateService.create(plate2);

        // Both should have the same date prefix
        String datePrefix = "BP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        assertTrue(saved1.getQrCode().startsWith(datePrefix));
        assertTrue(saved2.getQrCode().startsWith(datePrefix));

        // Second should have a higher sequence number
        String seq1 = saved1.getQrCode().substring(saved1.getQrCode().length() - 6);
        String seq2 = saved2.getQrCode().substring(saved2.getQrCode().length() - 6);
        assertTrue(Integer.parseInt(seq2) > Integer.parseInt(seq1),
                "Second QR code sequence should be higher: " + seq1 + " vs " + seq2);
    }

    @Test
    @DisplayName("create() should set default values for installCount, totalUsageDays, lifecycleStatus")
    void testDefaultValues() {
        BlindPlate plate = createTestPlate("BP-DEFAULT-001");
        BlindPlate saved = blindPlateService.create(plate);

        assertEquals(0, saved.getInstallCount());
        assertEquals(0.0, saved.getTotalUsageDays());
        assertEquals("normal", saved.getLifecycleStatus());
    }

    @Test
    @DisplayName("create() should reject duplicate code")
    void testDuplicateCodeRejection() {
        BlindPlate plate1 = createTestPlate("BP-DUP-001");
        blindPlateService.create(plate1);

        BlindPlate plate2 = createTestPlate("BP-DUP-001");
        BusinessException ex = assertThrows(BusinessException.class,
                () -> blindPlateService.create(plate2));
        assertEquals(400, ex.getCode());
        assertTrue(ex.getMessage().contains("编号已存在"));
    }

    // ==================== Status Change History ====================

    @Test
    @DisplayName("update() should record status history when status changes")
    void testStatusChangeHistoryRecorded() {
        BlindPlate plate = createTestPlate("BP-HIST-001");
        BlindPlate saved = blindPlateService.create(plate);

        // Change status
        BlindPlate update = new BlindPlate();
        update.setStatus("in_use");
        update.setLifecycleStatus("normal");
        blindPlateService.update(saved.getId(), update);

        List<BlindPlateStatusHistory> history = statusHistoryRepository
                .findByBlindPlateIdOrderByChangedAtDesc(saved.getId());
        assertFalse(history.isEmpty(), "Status history should be recorded");
        assertEquals("in_stock", history.get(0).getPreviousStatus());
        assertEquals("in_use", history.get(0).getNewStatus());
        assertEquals("system", history.get(0).getOperator());
    }

    @Test
    @DisplayName("update() should NOT record status history when status unchanged")
    void testNoHistoryWhenStatusUnchanged() {
        BlindPlate plate = createTestPlate("BP-HIST-002");
        plate.setName("Original Name");
        BlindPlate saved = blindPlateService.create(plate);

        // Update only name, keep same status
        BlindPlate update = new BlindPlate();
        update.setName("Updated Name");
        update.setStatus("in_stock");
        update.setLifecycleStatus("normal");
        blindPlateService.update(saved.getId(), update);

        List<BlindPlateStatusHistory> history = statusHistoryRepository
                .findByBlindPlateIdOrderByChangedAtDesc(saved.getId());
        // No status change, so no history for status
        // (might have lifecycle history if defaults differ, but status should be same)
        long statusChanges = history.stream()
                .filter(h -> "in_stock".equals(h.getPreviousStatus()) && "in_stock".equals(h.getNewStatus()))
                .count();
        assertEquals(0, statusChanges, "Should not record history for unchanged status");
    }

    // ==================== Inspection Alerts ====================

    @Test
    @DisplayName("getInspectionAlerts() should return plates with inspection_due or overdue lifecycle status")
    void testInspectionAlerts() {
        BlindPlate normalPlate = createTestPlate("BP-ALERT-001");
        normalPlate.setLifecycleStatus("normal");
        blindPlateService.create(normalPlate);

        BlindPlate duePlate = createTestPlate("BP-ALERT-002");
        duePlate.setLifecycleStatus("inspection_due");
        duePlate.setNextInspectionDate(LocalDate.now().plusDays(3));
        blindPlateService.create(duePlate);

        BlindPlate overduePlate = createTestPlate("BP-ALERT-003");
        overduePlate.setLifecycleStatus("overdue");
        overduePlate.setNextInspectionDate(LocalDate.now().minusDays(5));
        blindPlateService.create(overduePlate);

        List<BlindPlate> alerts = blindPlateService.getInspectionAlerts();
        assertEquals(2, alerts.size(), "Should return 2 plates with inspection_due or overdue");
        // Should be sorted by nextInspectionDate ASC
        assertTrue(alerts.stream().allMatch(p ->
                "inspection_due".equals(p.getLifecycleStatus()) || "overdue".equals(p.getLifecycleStatus())));
    }

    // ==================== Lifecycle Status Update ====================

    @Test
    @DisplayName("updateLifecycleStatus() should change status and record history")
    void testUpdateLifecycleStatus() {
        BlindPlate plate = createTestPlate("BP-LIFE-001");
        BlindPlate saved = blindPlateService.create(plate);
        assertEquals("normal", saved.getLifecycleStatus());

        blindPlateService.updateLifecycleStatus(saved.getId(), "inspection_due");

        BlindPlate updated = blindPlateService.findById(saved.getId());
        assertEquals("inspection_due", updated.getLifecycleStatus());

        List<BlindPlateStatusHistory> history = statusHistoryRepository
                .findByBlindPlateIdOrderByChangedAtDesc(saved.getId());
        assertFalse(history.isEmpty());
        assertEquals("normal", history.get(0).getPreviousStatus());
        assertEquals("inspection_due", history.get(0).getNewStatus());
    }

    @Test
    @DisplayName("updateLifecycleStatus() should NOT record history when status unchanged")
    void testUpdateLifecycleStatusNoChange() {
        BlindPlate plate = createTestPlate("BP-LIFE-002");
        BlindPlate saved = blindPlateService.create(plate);

        int historyBefore = statusHistoryRepository
                .findByBlindPlateIdOrderByChangedAtDesc(saved.getId()).size();

        blindPlateService.updateLifecycleStatus(saved.getId(), "normal");

        int historyAfter = statusHistoryRepository
                .findByBlindPlateIdOrderByChangedAtDesc(saved.getId()).size();
        assertEquals(historyBefore, historyAfter, "No new history should be recorded for unchanged status");
    }
}
