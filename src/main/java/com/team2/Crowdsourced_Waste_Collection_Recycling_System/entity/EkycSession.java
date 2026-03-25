package com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ekyc_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EkycSession {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "client_session", length = 255)
    private String clientSession;

    @Column(name = "token", length = 128)
    private String token;

    @Column(name = "type")
    private Integer type;

    @Column(name = "validate_postcode")
    private Boolean validatePostcode;

    @Column(name = "crop_param", length = 64)
    private String cropParam;

    @Column(name = "enhance")
    private Boolean enhance;

    @Column(name = "hash_front", length = 512)
    private String hashFront;

    @Column(name = "hash_back", length = 512)
    private String hashBack;

    @Column(name = "classify_ok")
    private Boolean classifyOk;

    @Column(name = "classify_code", length = 32)
    private String classifyCode;

    @Lob
    @Column(name = "classify_raw", columnDefinition = "LONGTEXT")
    private String classifyRaw;

    @Column(name = "liveness_ok")
    private Boolean livenessOk;

    @Column(name = "liveness_code", length = 32)
    private String livenessCode;

    @Lob
    @Column(name = "liveness_raw", columnDefinition = "LONGTEXT")
    private String livenessRaw;

    @Column(name = "ocr_front_ok")
    private Boolean ocrFrontOk;

    @Column(name = "ocr_front_code", length = 32)
    private String ocrFrontCode;

    @Lob
    @Column(name = "ocr_front_raw", columnDefinition = "LONGTEXT")
    private String ocrFrontRaw;

    @Column(name = "ocr_back_ok")
    private Boolean ocrBackOk;

    @Column(name = "ocr_back_code", length = 32)
    private String ocrBackCode;

    @Lob
    @Column(name = "ocr_back_raw", columnDefinition = "LONGTEXT")
    private String ocrBackRaw;

    @Column(name = "ocr_full_ok")
    private Boolean ocrFullOk;

    @Column(name = "ocr_full_code", length = 32)
    private String ocrFullCode;

    @Lob
    @Column(name = "ocr_full_raw", columnDefinition = "LONGTEXT")
    private String ocrFullRaw;

    @Column(name = "id_number", length = 32)
    private String idNumber;

    @Column(name = "citizen_id", length = 32)
    private String citizenId;

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "birth_day", length = 32)
    private String birthDay;

    @Column(name = "gender", length = 32)
    private String gender;

    @Column(name = "nationality", length = 64)
    private String nationality;

    @Column(name = "origin_location", length = 255)
    private String originLocation;

    @Column(name = "recent_location", length = 255)
    private String recentLocation;

    @Column(name = "issue_date", length = 32)
    private String issueDate;

    @Column(name = "issue_place", length = 255)
    private String issuePlace;

    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "back_type_id")
    private Integer backTypeId;

    @Column(name = "card_type", length = 255)
    private String cardType;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
