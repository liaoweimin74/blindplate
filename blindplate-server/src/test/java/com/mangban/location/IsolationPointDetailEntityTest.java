package com.mangban.location;

import com.mangban.location.entity.IsolationPointDetail;
import com.mangban.location.entity.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IsolationPointDetailEntityTest {
    @Test
    void entity_canBeInstantiatedAndFieldsWork() {
        IsolationPointDetail d = new IsolationPointDetail();
        Location loc = new Location();
        loc.setId(1L);
        d.setLocation(loc);
        d.setPidDiagramRef("PID-001.pdf");
        d.setMedium("H2");
        d.setPressure(2.5);
        d.setTemperature(150.0);
        d.setHazardLevel("A");
        d.setIsolationType("BLIND_PLATE");
        d.setCoordX(10.5);
        d.setCoordY(20.3);
        d.setCoordZ(5.0);
        d.setDiagramId(1L);

        assertEquals("PID-001.pdf", d.getPidDiagramRef());
        assertEquals("H2", d.getMedium());
        assertEquals(2.5, d.getPressure());
        assertEquals(150.0, d.getTemperature());
        assertEquals("A", d.getHazardLevel());
        assertEquals("BLIND_PLATE", d.getIsolationType());
        assertEquals(10.5, d.getCoordX());
        assertEquals(20.3, d.getCoordY());
        assertEquals(5.0, d.getCoordZ());
        assertEquals(1L, d.getDiagramId());
        assertEquals(1L, d.getLocation().getId());
    }
}
