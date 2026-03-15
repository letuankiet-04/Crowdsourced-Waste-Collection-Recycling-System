package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "collector_feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectorFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "feedback_code", nullable = false, unique = true, length = 20)
    private String feedbackCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "collector_id", nullable = false)
    private Collector collector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_request_id")
    private CollectionRequest collectionRequest;

    @Column(name = "feedback_type", nullable = false, length = 50)
    private String feedbackType;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "resolution")
    private String resolution;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
