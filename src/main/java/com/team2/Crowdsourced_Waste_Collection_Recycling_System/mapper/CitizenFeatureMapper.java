package com.team2.Crowdsourced_Waste_Collection_Recycling_System.mapper;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.CreateComplaintRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CitizenLeaderboardResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.CitizenRewardHistoryResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ComplaintResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Feedback;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointTransaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CitizenFeatureMapper {

    public CitizenRewardHistoryResponse toCitizenRewardHistoryResponse(PointTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        Integer reportId = null;
        String reportCode = null;
        if (transaction.getReport() != null) {
            reportId = transaction.getReport().getId();
            reportCode = transaction.getReport().getReportCode();
        } else if (transaction.getCollectionRequest() != null && transaction.getCollectionRequest().getReport() != null) {
            reportId = transaction.getCollectionRequest().getReport().getId();
            reportCode = transaction.getCollectionRequest().getReport().getReportCode();
        }

        Integer collectionId = transaction.getCollectionRequest() != null ? transaction.getCollectionRequest().getId() : null;

        return CitizenRewardHistoryResponse.builder()
                .reportId(reportId)
                .collectionId(collectionId)
                .reportCode(reportCode)
                .point(transaction.getPoints())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public CitizenLeaderboardResponse toCitizenLeaderboardResponse(Citizen citizen) {
        if (citizen == null) {
            return null;
        }
        return CitizenLeaderboardResponse.builder()
                .rank(null)
                .citizenId(citizen.getId())
                .fullName(citizen.getFullName())
                .ward(citizen.getWard())
                .city(citizen.getCity())
                .totalPoint(citizen.getTotalPoints() != null ? citizen.getTotalPoints() : 0)
                .build();
    }

    public ComplaintResponse toComplaintResponse(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        return ComplaintResponse.builder()
                .id(feedback.getId())
                .reportId(resolveReportId(feedback))
                .reportCode(resolveReportCode(feedback))
                .type(feedback.getFeedbackType())
                .content(feedback.getContent())
                .status(feedback.getStatus())
                .resolution(feedback.getResolution())
                .rating(feedback.getRating())
                .createdAt(feedback.getCreatedAt())
                .build();
    }

    public Integer resolveReportId(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        try {
            if (feedback.getCollectionRequest() == null || feedback.getCollectionRequest().getReport() == null) {
                return null;
            }
            return feedback.getCollectionRequest().getReport().getId();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public String resolveReportCode(Feedback feedback) {
        if (feedback == null) {
            return null;
        }
        try {
            if (feedback.getCollectionRequest() == null || feedback.getCollectionRequest().getReport() == null) {
                return null;
            }
            return feedback.getCollectionRequest().getReport().getReportCode();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public Feedback toFeedback(CreateComplaintRequest request) {
        if (request == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        Feedback fb = new Feedback();
        fb.setId(null);
        fb.setCitizen(null);
        fb.setCollectionRequest(null);
        fb.setFeedbackCode(null);
        fb.setCreatedAt(now);
        fb.setUpdatedAt(now);
        fb.setStatus("PENDING");

        String subject;
        if (request.getReportCode() != null) {
            subject = "Complaint for Report " + request.getReportCode() + " - " + request.getType();
        } else if (request.getReportId() != null) {
            subject = "Complaint for Report #" + request.getReportId() + " - " + request.getType();
        } else {
            subject = "General Complaint - " + request.getType();
        }
        fb.setSubject(subject);

        fb.setContent(request.getContent());
        fb.setFeedbackType(request.getType());
        fb.setRating(request.getRating());
        fb.setResolution(null);

        return fb;
    }
}
