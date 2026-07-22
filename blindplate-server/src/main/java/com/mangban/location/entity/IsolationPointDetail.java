package com.mangban.location.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bp_isolation_point_detail")
public class IsolationPointDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", unique = true, nullable = false)
    private Location location;

    @Column(name = "pid_diagram_ref", length = 200)
    private String pidDiagramRef;

    @Column(length = 50)
    private String medium;

    private Double pressure;

    private Double temperature;

    @Column(name = "hazard_level", length = 1)
    private String hazardLevel;

    @Column(name = "isolation_type", length = 30)
    private String isolationType;

    private Double coordX;

    private Double coordY;

    private Double coordZ;

    @Column(name = "diagram_id")
    private Long diagramId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
