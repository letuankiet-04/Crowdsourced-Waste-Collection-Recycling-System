package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentCreateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentUpdateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterprisePointAdjustmentResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.CollectionRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.PointTransaction;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.WasteReport;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.PointTransactionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterprisePointAdjustmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnterprisePointAdjustmentServiceImpl implements EnterprisePointAdjustmentService {
    private static final String TRANSACTION_TYPE = "ADJUST_ENTERPRISE";

    private final PointTransactionRepository pointTransactionRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final CitizenRepository citizenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EnterprisePointAdjustmentResponse create(Integer enterpriseId, String actorEmail, EnterprisePointAdjustmentCreateRequest request) {
        Integer points = request.getPoints();
        if (points == null || points == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points phải khác 0");
        }

        User actor = requireUser(actorEmail);
        CollectionRequest collectionRequest = requireCollectionRequestOfEnterprise(enterpriseId, request.getCollectionRequestId());
        WasteReport report = collectionRequest.getReport();

        Integer reportCitizenId = report != null && report.getCitizen() != null ? report.getCitizen().getId() : null;
        Integer citizenId = request.getCitizenId() != null ? request.getCitizenId() : reportCitizenId;
        if (citizenId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không xác định được citizenId");
        }
        if (reportCitizenId != null && !reportCitizenId.equals(citizenId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "citizenId không khớp với report của collectionRequest");
        }

        Citizen citizen = citizenRepository.findByIdForUpdate(citizenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen không tồn tại"));

        int currentPoints = citizen.getTotalPoints() != null ? citizen.getTotalPoints() : 0;
        int balanceAfter = currentPoints + points;
        if (balanceAfter < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điểm sau điều chỉnh không hợp lệ");
        }

        LocalDateTime now = LocalDateTime.now();

        citizen.setTotalPoints(balanceAfter);
        citizenRepository.save(citizen);

        PointTransaction tx = new PointTransaction();
        tx.setCitizen(citizen);
        tx.setCollectionRequest(collectionRequest);
        tx.setReport(report);
        tx.setPoints(points);
        tx.setTransactionType(TRANSACTION_TYPE);
        tx.setDescription(request.getDescription());
        tx.setBalanceAfter(balanceAfter);
        tx.setCreatedBy(actor);
        tx.setCreatedAt(now);

        PointTransaction saved = pointTransactionRepository.save(tx);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public EnterprisePointAdjustmentResponse update(Integer enterpriseId, String actorEmail, Integer transactionId, EnterprisePointAdjustmentUpdateRequest request) {
        PointTransaction tx = requireOwnedAdjustment(enterpriseId, actorEmail, transactionId);

        Integer points = request.getPoints();
        if (points == null || points == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "points phải khác 0");
        }

        Integer citizenId = tx.getCitizen() != null ? tx.getCitizen().getId() : null;
        if (citizenId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction thiếu citizen");
        }

        ensureLatestTransaction(citizenId, tx.getId());

        Citizen citizen = citizenRepository.findByIdForUpdate(citizenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen không tồn tại"));

        int currentPoints = citizen.getTotalPoints() != null ? citizen.getTotalPoints() : 0;
        int oldPoints = tx.getPoints() != null ? tx.getPoints() : 0;
        int delta = points - oldPoints;
        int balanceAfter = currentPoints + delta;
        if (balanceAfter < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điểm sau điều chỉnh không hợp lệ");
        }

        citizen.setTotalPoints(balanceAfter);
        citizenRepository.save(citizen);

        tx.setPoints(points);
        tx.setDescription(request.getDescription());
        tx.setBalanceAfter(balanceAfter);
        PointTransaction saved = pointTransactionRepository.save(tx);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Integer enterpriseId, String actorEmail, Integer transactionId) {
        PointTransaction tx = requireOwnedAdjustment(enterpriseId, actorEmail, transactionId);

        Integer citizenId = tx.getCitizen() != null ? tx.getCitizen().getId() : null;
        if (citizenId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction thiếu citizen");
        }

        ensureLatestTransaction(citizenId, tx.getId());

        Citizen citizen = citizenRepository.findByIdForUpdate(citizenId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Citizen không tồn tại"));

        int currentPoints = citizen.getTotalPoints() != null ? citizen.getTotalPoints() : 0;
        int points = tx.getPoints() != null ? tx.getPoints() : 0;
        int balanceAfter = currentPoints - points;
        if (balanceAfter < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Số điểm sau điều chỉnh không hợp lệ");
        }

        citizen.setTotalPoints(balanceAfter);
        citizenRepository.save(citizen);

        pointTransactionRepository.delete(tx);
    }

    private User requireUser(String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu email trong token");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User không tồn tại"));
    }

    private CollectionRequest requireCollectionRequestOfEnterprise(Integer enterpriseId, Integer collectionRequestId) {
        if (collectionRequestId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu collectionRequestId");
        }
        CollectionRequest collectionRequest = collectionRequestRepository.findById(collectionRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CollectionRequest không tồn tại"));

        Integer ownerId = collectionRequest.getEnterprise() != null ? collectionRequest.getEnterprise().getId() : null;
        if (ownerId == null || !ownerId.equals(enterpriseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền trên collectionRequest này");
        }
        return collectionRequest;
    }

    private PointTransaction requireOwnedAdjustment(Integer enterpriseId, String actorEmail, Integer transactionId) {
        PointTransaction tx = pointTransactionRepository.findOneWithDetailsById(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction không tồn tại"));

        if (!TRANSACTION_TYPE.equals(tx.getTransactionType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ hỗ trợ sửa/xóa transaction loại điều chỉnh của enterprise");
        }

        Integer ownerId = tx.getCollectionRequest() != null && tx.getCollectionRequest().getEnterprise() != null
                ? tx.getCollectionRequest().getEnterprise().getId()
                : null;
        if (ownerId == null || !ownerId.equals(enterpriseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Không có quyền trên transaction này");
        }

        String createdByEmail = tx.getCreatedBy() != null ? tx.getCreatedBy().getEmail() : null;
        if (createdByEmail == null || !createdByEmail.equalsIgnoreCase(actorEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ người tạo mới được sửa/xóa transaction này");
        }

        return tx;
    }

    private void ensureLatestTransaction(Integer citizenId, Integer transactionId) {
        PointTransaction latest = pointTransactionRepository.findTopByCitizenIdOrderByCreatedAtDesc(citizenId);
        if (latest == null || latest.getId() == null || !latest.getId().equals(transactionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Chỉ được sửa/xóa giao dịch điểm mới nhất của citizen");
        }
    }

    private EnterprisePointAdjustmentResponse toResponse(PointTransaction tx) {
        Integer citizenId = tx.getCitizen() != null ? tx.getCitizen().getId() : null;
        String citizenName = null;
        if (tx.getCitizen() != null) {
            if (tx.getCitizen().getUser() != null && tx.getCitizen().getUser().getFullName() != null) {
                citizenName = tx.getCitizen().getUser().getFullName();
            } else {
                citizenName = tx.getCitizen().getFullName();
            }
        }

        Integer requestId = tx.getCollectionRequest() != null ? tx.getCollectionRequest().getId() : null;
        Integer reportId = null;
        if (tx.getReport() != null) {
            reportId = tx.getReport().getId();
        } else if (tx.getCollectionRequest() != null && tx.getCollectionRequest().getReport() != null) {
            reportId = tx.getCollectionRequest().getReport().getId();
        }

        return EnterprisePointAdjustmentResponse.builder()
                .id(tx.getId())
                .citizenId(citizenId)
                .citizenName(citizenName)
                .collectionRequestId(requestId)
                .reportId(reportId)
                .points(tx.getPoints())
                .description(tx.getDescription())
                .balanceAfter(tx.getBalanceAfter())
                .createdByEmail(tx.getCreatedBy() != null ? tx.getCreatedBy().getEmail() : null)
                .createdAt(tx.getCreatedAt())
                .build();
    }
}

