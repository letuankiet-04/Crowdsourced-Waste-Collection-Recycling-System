package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recycling_enterprises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingEnterprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    private String email;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    @Column(name = "tax_code", length = 50)
    private String taxCode;

    @Column(length = 20)
    private String status = "active";

    @Column(name = "total_collected_weight")
    private BigDecimal totalCollectedWeight = BigDecimal.ZERO;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
