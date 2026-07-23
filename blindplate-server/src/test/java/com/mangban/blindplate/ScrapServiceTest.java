package com.mangban.blindplate;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateScrapRecord;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.repository.BlindPlateScrapRecordRepository;
import com.mangban.blindplate.service.ScrapService;
import com.mangban.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ScrapServiceTest {

    @Autowired
    private ScrapService scrapService;

    @Autowired
    private BlindPlateRepository blindPlateRepository;

    @Autowired
    private BlindPlateScrapRecordRepository scrapRecordRepository;

    private BlindPlate createAndSavePlate(String code, String status) {
        BlindPlate plate = new BlindPlate();
        plate.setCode(code);
        plate.setStatus(status);
        plate.setLifecycleStatus("normal");
        return blindPlateRepository.save(plate);
    }

    @BeforeEach
    void cleanUp() {
        scrapRecordRepository.deleteAll();
        blindPlateRepository.deleteAll();
    }

    // ==================== Scrap Validation ====================

    @Test
    @DisplayName("submitScrap() should reject if plate status is already 'scrapped'")
    void testRejectScrapForAlreadyScrapped() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-001", "scrapped");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> scrapService.submitScrap(plate.getId(), "张三", "测试报废"));
        assertEquals(400, ex.getCode());
        assertTrue(ex.getMessage().contains("该盲板已报废"));
    }

    @Test
    @DisplayName("submitScrap() should reject if pending scrap application already exists")
    void testRejectDuplicatePendingScrap() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-002", "in_stock");

        // First submission
        scrapService.submitScrap(plate.getId(), "张三", "第一次报废申请");

        // Second submission should be rejected
        BusinessException ex = assertThrows(BusinessException.class,
                () -> scrapService.submitScrap(plate.getId(), "李四", "第二次报废申请"));
        assertEquals(400, ex.getCode());
        assertTrue(ex.getMessage().contains("该盲板已有待审批的报废申请"));
    }

    @Test
    @DisplayName("submitScrap() should create pending scrap record with correct fields")
    void testSubmitScrapSuccess() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-003", "in_stock");

        BlindPlateScrapRecord record = scrapService.submitScrap(plate.getId(), "王五", "设备老化");

        assertNotNull(record.getId());
        assertEquals("pending", record.getStatus());
        assertEquals("王五", record.getApplicant());
        assertEquals("设备老化", record.getReason());
        assertNotNull(record.getApplyTime());
        assertEquals(plate.getId(), record.getBlindPlateId());
    }

    // ==================== Approval Transition ====================

    @Test
    @DisplayName("approveScrap() with approved=true should set plate status to 'scrapped' and lifecycle to 'scrapped'")
    void testApproveScrap() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-004", "in_stock");
        BlindPlateScrapRecord record = scrapService.submitScrap(plate.getId(), "赵六", "需要报废");

        BlindPlateScrapRecord approved = scrapService.approveScrap(record.getId(), true, "审批人", "同意报废");

        assertEquals("approved", approved.getStatus());
        assertEquals("审批人", approved.getApprover());
        assertEquals("同意报废", approved.getApproveComment());
        assertNotNull(approved.getApproveTime());

        BlindPlate updated = blindPlateRepository.findById(plate.getId()).orElseThrow();
        assertEquals("scrapped", updated.getStatus());
        assertEquals("scrapped", updated.getLifecycleStatus());
    }

    @Test
    @DisplayName("approveScrap() with approved=false should set scrap record to 'rejected' and NOT change plate status")
    void testRejectScrap() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-005", "in_stock");
        BlindPlateScrapRecord record = scrapService.submitScrap(plate.getId(), "钱七", "申请报废");

        BlindPlateScrapRecord rejected = scrapService.approveScrap(record.getId(), false, "审批人", "不同意");

        assertEquals("rejected", rejected.getStatus());

        BlindPlate updated = blindPlateRepository.findById(plate.getId()).orElseThrow();
        assertEquals("in_stock", updated.getStatus(), "Plate status should remain unchanged after rejection");
    }

    @Test
    @DisplayName("approveScrap() on non-existent record should throw BusinessException")
    void testApproveNonExistent() {
        assertThrows(BusinessException.class,
                () -> scrapService.approveScrap(99999L, true, "审批人", "comment"));
    }

    // ==================== Scrap History ====================

    @Test
    @DisplayName("findByBlindPlateId() should return scrap records ordered by applyTime DESC")
    void testFindByBlindPlateId() {
        BlindPlate plate = createAndSavePlate("BP-SCRAP-006", "in_stock");

        BlindPlateScrapRecord r1 = new BlindPlateScrapRecord();
        r1.setBlindPlateId(plate.getId());
        r1.setApplicant("A");
        r1.setReason("R1");
        r1.setStatus("rejected");
        r1.setApplyTime(java.time.LocalDateTime.now().minusDays(5));
        scrapRecordRepository.save(r1);

        BlindPlateScrapRecord r2 = new BlindPlateScrapRecord();
        r2.setBlindPlateId(plate.getId());
        r2.setApplicant("B");
        r2.setReason("R2");
        r2.setStatus("rejected");
        r2.setApplyTime(java.time.LocalDateTime.now().minusDays(1));
        scrapRecordRepository.save(r2);

        List<BlindPlateScrapRecord> result = scrapService.findByBlindPlateId(plate.getId());
        assertEquals(2, result.size());
        // Most recent should be first
        assertEquals("B", result.get(0).getApplicant());
    }
}
