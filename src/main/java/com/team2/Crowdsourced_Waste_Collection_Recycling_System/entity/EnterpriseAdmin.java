package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", referencedColumnName = "id")
    private RecyclingEnterprise recyclingEnterprise;

    private String position; // e.g., Manager, Staff
}
