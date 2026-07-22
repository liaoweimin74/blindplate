package com.mangban.location;

import com.mangban.location.entity.LocationChangeRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LocationChangeRecordEntityTest {
    @Test
    void entity_fieldsWorkCorrectly() {
        LocationChangeRecord r = new LocationChangeRecord();
        r.setLocationId(1L);
        r.setChangeType("UPDATE");
        r.setFieldName("name");
        r.setOldValue("old-name");
        r.setNewValue("new-name");
        r.setStatus("PENDING");
        r.setApplicantId(100L);

        assertEquals(1L, r.getLocationId());
        assertEquals("UPDATE", r.getChangeType());
        assertEquals("name", r.getFieldName());
        assertEquals("old-name", r.getOldValue());
        assertEquals("new-name", r.getNewValue());
        assertEquals("PENDING", r.getStatus());
        assertEquals(100L, r.getApplicantId());
    }
}
