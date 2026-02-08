package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "waste_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "wasteCategory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WasteType> wasteTypes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
