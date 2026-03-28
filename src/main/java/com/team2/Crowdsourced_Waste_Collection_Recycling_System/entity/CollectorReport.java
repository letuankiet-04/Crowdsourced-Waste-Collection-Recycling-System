package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "collector_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectorReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "report_code", unique = true, length = 20)
    private String reportCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collection_request_id", nullable = false)
    private CollectionRequest collectionRequest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CollectorReportStatus status;

    @Column(name = "collector_note", length = 1000)
    private String collectorNote;

    @Column(name = "total_point")
    private Integer totalPoint;

    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
