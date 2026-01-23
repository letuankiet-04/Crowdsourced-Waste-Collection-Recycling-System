package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "role_code", nullable = false, length = 20, unique = true)
    private String roleCode;
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;
    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "is_active")
    private Boolean isActive = true;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
