package com.mangban.blindplate;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateInspection;
import com.mangban.blindplate.repository.BlindPlateInspectionRepository;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.service.BlindPlateService;
import com.mangban.blindplate.service.InspectionService;
import com.mangban.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InspectionServiceTest {

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private BlindPlateService blindPlateService;

    @Autowired
    private BlindPlateRepository blindPlateRepository;

    @Autowired
    private BlindPlateInspectionRepository inspectionRepository;

    private BlindPlate createAndSavePlate(String code, LocalDate nextInspectionDate) {
        BlindPlate plate = new BlindPlate();
        plate.setCode(code);
        plate.setStatus("in_stock");
        plate.setLifecycleStatus("normal");
        plate.setNextInspectionDate(nextInspectionDate);
        return blindPlateRepository.save(plate);
    }

    @BeforeEach
    void cleanUp() {
        inspectionRepository.deleteAll();
        blindPlateRepository.deleteAll();
    }

    // ==================== Lifecycle Status Calculation ====================

    @Test
    @DisplayName("create() inspection with qualified result and future nextInspectionDate should set lifecycle to normal")
    void testQualifiedInspectionFutureDate() {
        BlindPlate plate = createAndSavePlate("BP-INSP-001", LocalDate.now().plusDays(30));
        plate.setLifecycleStatus("overdue");
        blindPlateRepository.save(plate);

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(LocalDate.now().plusDays(180));
        inspection.setInspector("张三");

        inspectionService.create(plate.getId(), inspection);

        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals("normal", updated.getLifecycleStatus(),
                "Qualified inspection with future date should set lifecycle to normal");
        assertEquals(LocalDate.now().plusDays(180), updated.getNextInspectionDate());
    }

    @Test
    @DisplayName("create() inspection where nextInspectionDate <= today+7 should set lifecycle to inspection_due")
    void testInspectionDueSoon() {
        BlindPlate plate = createAndSavePlate("BP-INSP-002", LocalDate.now().plusDays(30));

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(LocalDate.now().plusDays(5)); // within 7 days
        inspection.setInspector("李四");

        inspectionService.create(plate.getId(), inspection);

        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals("inspection_due", updated.getLifecycleStatus(),
                "nextInspectionDate within 7 days should set lifecycle to inspection_due");
    }

    @Test
    @DisplayName("create() inspection where nextInspectionDate < today should set lifecycle to overdue")
    void testInspectionOverdue() {
        BlindPlate plate = createAndSavePlate("BP-INSP-003", LocalDate.now().plusDays(30));

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(LocalDate.now().minusDays(1)); // past
        inspection.setInspector("王五");

        inspectionService.create(plate.getId(), inspection);

        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals("overdue", updated.getLifecycleStatus(),
                "nextInspectionDate before today should set lifecycle to overdue");
    }

    @Test
    @DisplayName("create() should update BlindPlate.nextInspectionDate from inspection record")
    void testNextInspectionDateUpdated() {
        BlindPlate plate = createAndSavePlate("BP-INSP-004", LocalDate.now().plusDays(30));
        LocalDate newNextDate = LocalDate.now().plusDays(90);

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(newNextDate);
        inspection.setInspector("赵六");

        BlindPlateInspection saved = inspectionService.create(plate.getId(), inspection);

        assertNotNull(saved.getId());
        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals(newNextDate, updated.getNextInspectionDate());
    }

    @Test
    @DisplayName("create() with null nextInspectionDate should set lifecycle to normal")
    void testNullNextInspectionDate() {
        BlindPlate plate = createAndSavePlate("BP-INSP-005", LocalDate.now().plusDays(10));
        plate.setLifecycleStatus("overdue");
        blindPlateRepository.save(plate);

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(null);
        inspection.setInspector("钱七");

        inspectionService.create(plate.getId(), inspection);

        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals("normal", updated.getLifecycleStatus(),
                "Null nextInspectionDate should set lifecycle to normal");
    }

    // ==================== Inspection CRUD ====================

    @Test
    @DisplayName("findByBlindPlateId() should return inspections ordered by inspectionDate DESC")
    void testFindByBlindPlateId() {
        BlindPlate plate = createAndSavePlate("BP-INSP-006", null);

        BlindPlateInspection ins1 = new BlindPlateInspection();
        ins1.setBlindPlateId(plate.getId());
        ins1.setInspectionDate(LocalDate.of(2024, 1, 15));
        ins1.setResult("qualified");
        ins1.setInspector("A");
        inspectionRepository.save(ins1);

        BlindPlateInspection ins2 = new BlindPlateInspection();
        ins2.setBlindPlateId(plate.getId());
        ins2.setInspectionDate(LocalDate.of(2024, 6, 20));
        ins2.setResult("qualified");
        ins2.setInspector("B");
        inspectionRepository.save(ins2);

        List<BlindPlateInspection> result = inspectionService.findByBlindPlateId(plate.getId());
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2024, 6, 20), result.get(0).getInspectionDate(),
                "Most recent inspection should be first");
    }

    @Test
    @DisplayName("update() should update inspection fields and recalculate lifecycle")
    void testUpdateInspection() {
        BlindPlate plate = createAndSavePlate("BP-INSP-007", LocalDate.now().plusDays(30));

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(LocalDate.now().plusDays(180));
        inspection.setInspector("original");
        BlindPlateInspection saved = inspectionService.create(plate.getId(), inspection);

        // Update
        BlindPlateInspection update = new BlindPlateInspection();
        update.setNextInspectionDate(LocalDate.now().plusDays(3)); // within 7 days
        inspectionService.update(saved.getId(), update);

        BlindPlate updated = blindPlateService.findById(plate.getId());
        assertEquals("inspection_due", updated.getLifecycleStatus());
    }

    @Test
    @DisplayName("delete() should remove inspection and recalculate lifecycle")
    void testDeleteInspection() {
        BlindPlate plate = createAndSavePlate("BP-INSP-008", LocalDate.now().plusDays(30));

        BlindPlateInspection inspection = new BlindPlateInspection();
        inspection.setInspectionDate(LocalDate.now());
        inspection.setResult("qualified");
        inspection.setNextInspectionDate(LocalDate.now().plusDays(180));
        BlindPlateInspection saved = inspectionService.create(plate.getId(), inspection);

        inspectionService.delete(saved.getId());

        List<BlindPlateInspection> remaining = inspectionService.findByBlindPlateId(plate.getId());
        assertEquals(0, remaining.size());
    }

    @Test
    @DisplayName("update() on non-existent inspection should throw BusinessException")
    void testUpdateNonExistent() {
        BlindPlateInspection update = new BlindPlateInspection();
        update.setResult("qualified");
        assertThrows(BusinessException.class, () -> inspectionService.update(99999L, update));
    }
}
