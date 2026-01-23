package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citizens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Citizen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String ward;

    @Column(length = 100)
    private String district;

    @Column(length = 100)
    private String city;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "total_reports")
    private Integer totalReports = 0;

    @Column(name = "valid_reports")
    private Integer validReports = 0;
}