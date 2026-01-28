package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "collectors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collector {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enterprise_id", nullable = false)
    private RecyclingEnterprise enterprise;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    @Column(length = 20)
    private String status = "available";

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    @Column(name = "total_collections")
    private Integer totalCollections = 0;

    @Column(name = "successful_collections")
    private Integer successfulCollections = 0;

    @Column(name = "total_weight_collected", precision = 12, scale = 2)
    private BigDecimal totalWeightCollected = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (status == null) {
            status = "available";
        }
        if (totalCollections == null) {
            totalCollections = 0;
        }
        if (successfulCollections == null) {
            successfulCollections = 0;
        }
        if (totalWeightCollected == null) {
            totalWeightCollected = BigDecimal.ZERO;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
