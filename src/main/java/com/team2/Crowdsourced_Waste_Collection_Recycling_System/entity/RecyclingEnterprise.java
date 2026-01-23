package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "recycling_enterprises")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecyclingEnterprise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "enterprise_name")
    private String enterpriseName;
    
    @Column(name = "tax_code")
    private String taxCode;
    
    @Column(name = "license_number")
    private String licenseNumber;

    private String address;
}
