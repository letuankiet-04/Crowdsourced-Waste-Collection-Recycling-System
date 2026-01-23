package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "enterprise_admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false)
    private RecyclingEnterprise enterprise;

    @Column(length = 100)
    private String position;

    @Column(name = "is_owner")
    private Boolean isOwner = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
