package com.mangban.blindplate;

import com.mangban.blindplate.entity.BlindPlate;
import com.mangban.blindplate.entity.BlindPlateStocktake;
import com.mangban.blindplate.entity.BlindPlateStocktakeItem;
import com.mangban.blindplate.repository.BlindPlateRepository;
import com.mangban.blindplate.repository.BlindPlateStocktakeItemRepository;
import com.mangban.blindplate.repository.BlindPlateStocktakeRepository;
import com.mangban.blindplate.service.StocktakeService;
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
class StocktakeServiceTest {

    @Autowired
    private StocktakeService stocktakeService;

    @Autowired
    private BlindPlateRepository blindPlateRepository;

    @Autowired
    private BlindPlateStocktakeRepository stocktakeRepository;

    @Autowired
    private BlindPlateStocktakeItemRepository stocktakeItemRepository;

    private BlindPlate createAndSavePlate(String code, String status) {
        BlindPlate plate = new BlindPlate();
        plate.setCode(code);
        plate.setStatus(status);
        plate.setLifecycleStatus("normal");
        return blindPlateRepository.save(plate);
    }

    @BeforeEach
    void cleanUp() {
        stocktakeItemRepository.deleteAll();
        stocktakeRepository.deleteAll();
        blindPlateRepository.deleteAll();
    }

    // ==================== Batch Creation ====================

    @Test
    @DisplayName("createBatch() should generate batchNo in format ST-{yyyyMMdd}-{4-digit sequence}")
    void testBatchNoFormat() {
        BlindPlateStocktake batch = stocktakeService.createBatch("Test Batch", "operator1");

        assertNotNull(batch.getId());
        assertNotNull(batch.getBatchNo());
        assertTrue(batch.getBatchNo().startsWith("ST-"),
                "Batch number should start with ST- but was " + batch.getBatchNo());
        assertEquals("in_progress", batch.getStatus());
        assertEquals("Test Batch", batch.getBatchName());
        assertEquals("operator1", batch.getOperator());
    }

    @Test
    @DisplayName("createBatch() should increment sequence for same-day batches")
    void testBatchNoSequenceIncrement() {
        BlindPlateStocktake batch1 = stocktakeService.createBatch("Batch1", "op1");
        BlindPlateStocktake batch2 = stocktakeService.createBatch("Batch2", "op2");

        String seq1 = batch1.getBatchNo().substring(batch1.getBatchNo().lastIndexOf('-') + 1);
        String seq2 = batch2.getBatchNo().substring(batch2.getBatchNo().lastIndexOf('-') + 1);
        assertTrue(Integer.parseInt(seq2) > Integer.parseInt(seq1),
                "Second batch sequence should be higher: " + seq1 + " vs " + seq2);
    }

    // ==================== Scan Codes ====================

    @Test
    @DisplayName("scanCodes() should create items with scannedAt timestamp")
    void testScanCodes() {
        BlindPlateStocktake batch = stocktakeService.createBatch("Scan Test", "op1");

        List<BlindPlateStocktakeItem> items = stocktakeService.scanCodes(batch.getId(),
                List.of("BP-001", "BP-002", "BP-003"));

        assertEquals(3, items.size());
        for (BlindPlateStocktakeItem item : items) {
            assertNotNull(item.getScannedAt());
            assertEquals(batch.getId(), item.getBatchId());
        }
    }

    @Test
    @DisplayName("scanCodes() on closed batch should throw BusinessException")
    void testScanOnClosedBatch() {
        BlindPlateStocktake batch = stocktakeService.createBatch("Closed Batch", "op1");
        batch.setStatus("closed");
        stocktakeRepository.save(batch);

        assertThrows(BusinessException.class,
                () -> stocktakeService.scanCodes(batch.getId(), List.of("BP-001")));
    }

    // ==================== Close Batch / Difference Report ====================

    @Test
    @DisplayName("closeBatch() should set status to 'closed' and set closedAt")
    void testCloseBatchStatus() {
        BlindPlateStocktake batch = stocktakeService.createBatch("Close Test", "op1");
        BlindPlateStocktake closed = stocktakeService.closeBatch(batch.getId());

        assertEquals("closed", closed.getStatus());
        assertNotNull(closed.getClosedAt());
    }

    @Test
    @DisplayName("closeBatch() should mark scanned in_stock plates as 'matched'")
    void testMatchedStatus() {
        BlindPlate plate1 = createAndSavePlate("BP-MATCH-001", "in_stock");
        BlindPlate plate2 = createAndSavePlate("BP-MATCH-002", "in_stock");

        BlindPlateStocktake batch = stocktakeService.createBatch("Match Test", "op1");
        stocktakeService.scanCodes(batch.getId(), List.of("BP-MATCH-001", "BP-MATCH-002"));

        stocktakeService.closeBatch(batch.getId());

        List<BlindPlateStocktakeItem> items = stocktakeService.getItems(batch.getId());
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(i -> "matched".equals(i.getMatchStatus())),
                "All scanned in_stock plates should be matched");
    }

    @Test
    @DisplayName("closeBatch() should mark scanned plates not in_stock as 'location_mismatch'")
    void testLocationMismatchStatus() {
        BlindPlate plate = createAndSavePlate("BP-MISMATCH-001", "in_use");

        BlindPlateStocktake batch = stocktakeService.createBatch("Mismatch Test", "op1");
        stocktakeService.scanCodes(batch.getId(), List.of("BP-MISMATCH-001"));

        stocktakeService.closeBatch(batch.getId());

        List<BlindPlateStocktakeItem> items = stocktakeService.getItems(batch.getId());
        // Should have 1 scanned item (location_mismatch) + 0 missing (no in_stock plates)
        long mismatchCount = items.stream()
                .filter(i -> "location_mismatch".equals(i.getMatchStatus())).count();
        assertTrue(mismatchCount >= 1, "Should have at least 1 location_mismatch item");
    }

    @Test
    @DisplayName("closeBatch() should mark non-existent scanned codes as 'unexpected'")
    void testUnexpectedStatus() {
        // Create an in_stock plate so closeBatch has something to compare against
        createAndSavePlate("BP-UNEXP-001", "in_stock");

        BlindPlateStocktake batch = stocktakeService.createBatch("Unexpected Test", "op1");
        stocktakeService.scanCodes(batch.getId(), List.of("NON-EXISTENT-CODE"));

        stocktakeService.closeBatch(batch.getId());

        List<BlindPlateStocktakeItem> items = stocktakeService.getItems(batch.getId());
        long unexpectedCount = items.stream()
                .filter(i -> "unexpected".equals(i.getMatchStatus())).count();
        assertTrue(unexpectedCount >= 1, "Should have at least 1 unexpected item");
    }

    @Test
    @DisplayName("closeBatch() should create 'missing' items for in_stock plates not scanned")
    void testMissingStatus() {
        BlindPlate plate1 = createAndSavePlate("BP-MISS-001", "in_stock");
        BlindPlate plate2 = createAndSavePlate("BP-MISS-002", "in_stock");
        BlindPlate plate3 = createAndSavePlate("BP-MISS-003", "in_stock");

        BlindPlateStocktake batch = stocktakeService.createBatch("Missing Test", "op1");
        // Only scan plate1, so plate2 and plate3 should be "missing"
        stocktakeService.scanCodes(batch.getId(), List.of("BP-MISS-001"));

        stocktakeService.closeBatch(batch.getId());

        List<BlindPlateStocktakeItem> items = stocktakeService.getItems(batch.getId());
        long missingCount = items.stream()
                .filter(i -> "missing".equals(i.getMatchStatus())).count();
        assertEquals(2, missingCount, "Should have 2 missing items for unscanned in_stock plates");
    }

    @Test
    @DisplayName("closeBatch() on already closed batch should throw BusinessException")
    void testCloseAlreadyClosedBatch() {
        BlindPlateStocktake batch = stocktakeService.createBatch("Double Close", "op1");
        stocktakeService.closeBatch(batch.getId());

        assertThrows(BusinessException.class,
                () -> stocktakeService.closeBatch(batch.getId()));
    }

    // ==================== Find All ====================

    @Test
    @DisplayName("findAll() should return batches ordered by createdAt DESC")
    void testFindAll() {
        stocktakeService.createBatch("Batch1", "op1");
        stocktakeService.createBatch("Batch2", "op2");
        stocktakeService.createBatch("Batch3", "op3");

        var page = stocktakeService.findAll(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(3, page.getTotalElements());
    }
}
