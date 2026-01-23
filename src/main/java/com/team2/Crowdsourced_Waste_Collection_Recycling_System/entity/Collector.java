package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_plate")
    private String vehiclePlate;

    private String status; // e.g., AVAILABLE, BUSY, OFFLINE
}
