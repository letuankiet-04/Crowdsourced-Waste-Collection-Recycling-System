package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Quy tắc tính điểm thưởng — singleton (luôn chỉ có 1 bản ghi, id = 1).
 * Admin chỉ có thể đọc và cập nhật nội dung văn bản.
 */
@Entity
@Table(name = "point_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointRule {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    /** Văn bản mô tả quy tắc tính điểm (hỗ trợ nội dung dài) */
    @Column(name = "content", columnDefinition = "TEXT")
    @Nationalized
    private String content;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
