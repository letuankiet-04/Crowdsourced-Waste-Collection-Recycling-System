package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCitizenAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCollectorAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateEnterpriseAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateAdminProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCitizenProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateCollectorProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.UpdateEnterpriseProfileRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminUserResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.DeleteUserPreviewResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.VehicleType;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.AppException;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.EkycSessionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionRequestRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectionTrackingRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback.CollectorFeedbackRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.feedback.FeedbackRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.LeaderboardRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.PointTransactionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.reward.VoucherRedemptionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.ReportImageRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportItemRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.waste.WasteReportRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.AdminAccountService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAccountServiceImpl implements AdminAccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CitizenRepository citizenRepository;
    private final CollectorRepository collectorRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;

    // Repositories cần cho hard-delete
    private final EkycSessionRepository ekycSessionRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final VoucherRedemptionRepository voucherRedemptionRepository;
    private final FeedbackRepository feedbackRepository;
    private final WasteReportRepository wasteReportRepository;
    private final ReportImageRepository reportImageRepository;
    private final WasteReportItemRepository wasteReportItemRepository;
    private final CollectorReportImageRepository collectorReportImageRepository;
    private final CollectorReportItemRepository collectorReportItemRepository;
    private final CollectorReportRepository collectorReportRepository;
    private final CollectorFeedbackRepository collectorFeedbackRepository;
    private final CollectionTrackingRepository collectionTrackingRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final LeaderboardRepository leaderboardRepository;

    @Override
    @Transactional
    public AdminUserResponse createCitizenAccount(AdminCreateCitizenAccountRequest request, String adminEmail) {
        validateBaseFields(request.getEmail(), request.getPassword(), request.getFullName());
        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(),
                request.getPhone(),
                "CITIZEN");

        Citizen citizen = new Citizen();
        citizen.setUser(savedUser);
        citizen.setEmail(savedUser.getEmail());
        citizen.setFullName(savedUser.getFullName());
        citizen.setPasswordHash(savedUser.getPasswordHash());
        citizen.setPhone(savedUser.getPhone());
        citizen.setAddress(request.getCitizenAddress());
        citizen.setWard(request.getCitizenWard());
        citizen.setCity(request.getCitizenCity());
        citizen.setTotalPoints(0);
        citizen.setTotalReports(0);
        citizen.setValidReports(0);
        citizenRepository.save(citizen);

        log.info("Admin {} đã tạo user id={} với role=CITIZEN", adminEmail, savedUser.getId());
        return toResponse(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse createCollectorAccount(AdminCreateCollectorAccountRequest request, String adminEmail) {
        validateBaseFields(request.getEmail(), request.getPassword(), request.getFullName());
        if (request.getEnterpriseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enterpriseId là bắt buộc với role COLLECTOR");
        }

        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(),
                request.getPhone(),
                "COLLECTOR");

        Enterprise enterprise = enterpriseRepository.findById(request.getEnterpriseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại"));

        Collector collector = new Collector();
        collector.setUser(savedUser);
        collector.setEnterprise(enterprise);
        collector.setEmail(savedUser.getEmail());
        collector.setFullName(savedUser.getFullName());
        collector.setEmployeeCode(request.getEmployeeCode());
        collector.setVehiclePlate(request.getVehiclePlate());
        collector.setStatus(CollectorStatus.OFFLINE);
        collector.setCreatedAt(LocalDateTime.now());

        if (request.getVehicleType() != null && !request.getVehicleType().isBlank()) {
            VehicleType vehicleType = VehicleType.fromString(request.getVehicleType());
            if (vehicleType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "vehicleType không hợp lệ (CAR|TRUCK|MOTORBIKE)");
            }
            collector.setVehicleType(vehicleType.name());
        }

        Collector savedCollector = collectorRepository.save(collector);
        if (savedCollector.getEmployeeCode() == null || savedCollector.getEmployeeCode().isBlank()) {
            savedCollector.setEmployeeCode(String.format("C%03d", savedCollector.getId()));
            collectorRepository.save(savedCollector);
        }

        log.info("Admin {} đã tạo user id={} với role=COLLECTOR", adminEmail, savedUser.getId());
        return toResponse(savedUser);
    }

    @Override
    @Transactional
    public AdminUserResponse createEnterpriseAccount(AdminCreateEnterpriseAccountRequest request, String adminEmail) {
        validateBaseFields(request.getEmail(), request.getPassword(), request.getFullName());
        if (request.getEnterpriseName() == null || request.getEnterpriseName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enterpriseName là bắt buộc với role ENTERPRISE");
        }

        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(),
                request.getPhone(),
                "ENTERPRISE");

        Enterprise enterprise = new Enterprise();
        enterprise.setName(request.getEnterpriseName().trim());
        enterprise.setAddress(request.getEnterpriseAddress());
        enterprise.setPhone(request.getEnterprisePhone() != null ? request.getEnterprisePhone() : savedUser.getPhone());
        enterprise.setEmail(request.getEnterpriseEmail() != null ? request.getEnterpriseEmail() : savedUser.getEmail());
        enterprise.setStatus("active");
        enterprise.setCreatedAt(LocalDateTime.now());
        enterprise.setUpdatedAt(LocalDateTime.now());

        Enterprise savedEnterprise = enterpriseRepository.save(enterprise);
        savedUser.setEnterprise(savedEnterprise);
        userRepository.save(savedUser);

        log.info("Admin {} đã tạo user id={} với role=ENTERPRISE", adminEmail, savedUser.getId());
        return toResponse(savedUser);
    }

    // ─────────────────────────────────────────────
    // Xem danh sách tài khoản
    // ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<AdminUserResponse> getAllUsers(String status, String roleCode, String adminEmail) {
        List<User> users;

        boolean hasStatus = status != null && !status.isBlank();
        boolean hasRole = roleCode != null && !roleCode.isBlank();

        if (hasStatus && hasRole) {
            users = userRepository.findAllByStatusAndRole_RoleCodeOrderByCreatedAtDesc(status, roleCode);
        } else if (hasStatus) {
            users = userRepository.findAllByStatusOrderByCreatedAtDesc(status);
        } else if (hasRole) {
            users = userRepository.findAllByRole_RoleCodeOrderByCreatedAtDesc(roleCode);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .filter(user -> adminEmail == null || user.getEmail() == null
                        || !user.getEmail().equalsIgnoreCase(adminEmail))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    // Xem chi tiết 1 tài khoản
    // ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserDetail(Integer userId) {
        User user = findUserById(userId);
        return toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateMyProfile(String adminEmail, UpdateAdminProfileRequest request) {
        if (adminEmail == null || adminEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu token");
        }
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu dữ liệu");
        }

        User adminUser = userRepository.findOneWithRoleByEmailIgnoreCase(adminEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String roleCode = adminUser.getRole() != null ? adminUser.getRole().getRoleCode() : null;
        if (roleCode == null || !"ADMIN".equalsIgnoreCase(roleCode)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Chỉ ADMIN mới được cập nhật hồ sơ này");
        }

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            if (fullName.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống");
            }
            adminUser.setFullName(fullName);
        }

        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            if (email.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
            }
            validateDuplicateEmailForUpdate(adminUser.getId(), email);
            adminUser.setEmail(email);
        }

        User saved = userRepository.save(adminUser);
        log.info("Admin {} đã cập nhật hồ sơ cá nhân", adminEmail);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AdminUserResponse updateCitizenProfile(Integer userId, UpdateCitizenProfileRequest request, String adminEmail) {
        User user = findUserById(userId);
        validateRoleForProfileUpdate(user, "CITIZEN");

        Citizen citizen = citizenRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CITIZEN_NOT_FOUND));

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            user.setFullName(fullName);
            citizen.setFullName(fullName);
        }
        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            validateDuplicateEmailForUpdate(userId, email);
            user.setEmail(email);
            citizen.setEmail(email);
        }
        if (request.getPhone() != null) {
            String phone = request.getPhone().trim();
            user.setPhone(phone);
            citizen.setPhone(phone);
        }
        if (request.getAddress() != null) {
            citizen.setAddress(request.getAddress().trim());
        }
        if (request.getWard() != null) {
            citizen.setWard(request.getWard().trim());
        }
        if (request.getCity() != null) {
            citizen.setCity(request.getCity().trim());
        }

        userRepository.save(user);
        citizenRepository.save(citizen);

        log.info("Admin {} đã cập nhật hồ sơ CITIZEN cho user id={}", adminEmail, userId);
        return toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateCollectorProfile(Integer userId, UpdateCollectorProfileRequest request, String adminEmail) {
        User user = findUserById(userId);
        validateRoleForProfileUpdate(user, "COLLECTOR");

        Collector collector = collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTOR_NOT_FOUND));

        if (request.getFullName() != null) {
            String fullName = request.getFullName().trim();
            user.setFullName(fullName);
            collector.setFullName(fullName);
        }
        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            validateDuplicateEmailForUpdate(userId, email);
            user.setEmail(email);
            collector.setEmail(email);
        }
        if (request.getVehicleType() != null) {
            VehicleType vehicleType = VehicleType.fromString(request.getVehicleType().trim());
            if (vehicleType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "vehicleType không hợp lệ (CAR|TRUCK|MOTORBIKE)");
            }
            collector.setVehicleType(vehicleType.name());
        }
        if (request.getVehiclePlate() != null) {
            collector.setVehiclePlate(request.getVehiclePlate().trim());
        }

        userRepository.save(user);
        collectorRepository.save(collector);

        log.info("Admin {} đã cập nhật hồ sơ COLLECTOR cho user id={}", adminEmail, userId);
        return toResponse(user);
    }

    @Override
    @Transactional
    public AdminUserResponse updateEnterpriseProfile(Integer userId, UpdateEnterpriseProfileRequest request,
            String adminEmail) {
        User user = findUserById(userId);
        validateRoleForProfileUpdate(user, "ENTERPRISE");

        Enterprise linkedEnterprise = user.getEnterprise();
        if (linkedEnterprise == null || linkedEnterprise.getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại");
        }

        Enterprise enterprise = enterpriseRepository.findById(linkedEnterprise.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enterprise không tồn tại"));

        if (request.getName() != null) {
            enterprise.setName(request.getName().trim());
        }
        if (request.getAddress() != null) {
            enterprise.setAddress(request.getAddress().trim());
        }
        if (request.getPhone() != null) {
            String phone = request.getPhone().trim();
            enterprise.setPhone(phone);
            user.setPhone(phone);
        }
        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            validateDuplicateEmailForUpdate(userId, email);
            enterprise.setEmail(email);
            user.setEmail(email);
        }
        if (request.getServiceWards() != null) {
            enterprise.setServiceWards(request.getServiceWards().trim());
        }
        if (request.getServiceCities() != null) {
            enterprise.setServiceCities(request.getServiceCities().trim());
        }

        enterpriseRepository.save(enterprise);
        userRepository.save(user);

        log.info("Admin {} đã cập nhật hồ sơ ENTERPRISE cho user id={}", adminEmail, userId);
        return toResponse(user);
    }

    // ─────────────────────────────────────────────
    // Khóa tài khoản
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public AdminUserResponse suspendUser(Integer userId, String adminEmail) {
        // Guard: Admin không thể tự khóa chính mình
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (userId.equals(adminUser.getId())) {
            throw new AppException(ErrorCode.CANNOT_SUSPEND_SELF);
        }

        User user = findUserById(userId);

        if ("suspended".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_ALREADY_SUSPENDED);
        }

        user.setStatus("suspended");
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        if ("COLLECTOR".equalsIgnoreCase(roleCode)) {
            Collector collector = collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTOR_NOT_FOUND));
            collector.setStatus(CollectorStatus.SUSPEND);
            collectorRepository.save(collector);
        }
        User saved = userRepository.save(user);

        log.info("Admin {} đã khóa tài khoản user id={}", adminEmail, userId);
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // Mở tài khoản
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public AdminUserResponse activateUser(Integer userId, String adminEmail) {
        User user = findUserById(userId);

        if ("active".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_ALREADY_ACTIVE);
        }

        user.setStatus("active");
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        if ("COLLECTOR".equalsIgnoreCase(roleCode)) {
            Collector collector = collectorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.COLLECTOR_NOT_FOUND));
            collector.setStatus(CollectorStatus.OFFLINE);
            collectorRepository.save(collector);
        }
        User saved = userRepository.save(user);

        log.info("Admin {} đã mở khóa tài khoản user id={}", adminEmail, userId);
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // Preview data trước khi hard-delete
    // ─────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public DeleteUserPreviewResponse previewDeleteUser(Integer userId, String adminEmail) {
        User user = findUserById(userId);
        validateDeleteGuards(user, adminEmail);

        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : "";

        DeleteUserPreviewResponse.DeleteUserPreviewResponseBuilder builder = DeleteUserPreviewResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roleCode(roleCode)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt());

        if ("CITIZEN".equalsIgnoreCase(roleCode)) {
            Optional<Citizen> citizenOpt = citizenRepository.findByUserId(userId);
            if (citizenOpt.isPresent()) {
                Integer citizenId = citizenOpt.get().getId();
                builder.wasteReportCount(wasteReportRepository.countByCitizen_Id(citizenId))
                        .feedbackCount(feedbackRepository.countByCitizenId(citizenId))
                        .pointTransactionCount(pointTransactionRepository.countByCitizenId(citizenId))
                        .voucherRedemptionCount(voucherRedemptionRepository.countByCitizen_Id(citizenId))
                        .leaderboardEntryCount(leaderboardRepository.countByCitizenId(citizenId));
            }
        } else if ("COLLECTOR".equalsIgnoreCase(roleCode)) {
            Optional<Collector> collectorOpt = collectorRepository.findByUserId(userId);
            if (collectorOpt.isPresent()) {
                Integer collectorId = collectorOpt.get().getId();
                builder.collectorReportCount(collectorReportRepository.countByCollector_Id(collectorId))
                        .collectionRequestCount(collectionRequestRepository.countByCollector_Id(collectorId))
                        .collectorFeedbackCount(collectorFeedbackRepository.countByCollector_Id(collectorId));
            }
        }

        return builder.build();
    }

    // ─────────────────────────────────────────────
    // Hard-delete tài khoản
    // ─────────────────────────────────────────────

    @Override
    @Transactional
    public void hardDeleteUser(Integer userId, String adminEmail) {
        User user = findUserById(userId);
        validateDeleteGuards(user, adminEmail);

        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : "";

        // 1. Xóa EkycSession (tham chiếu trực tiếp User)
        ekycSessionRepository.deleteByUser_Id(userId);

        // 2. Xóa PointTransaction theo createdBy (tham chiếu trực tiếp User)
        pointTransactionRepository.deleteByCreatedBy_Id(userId);

        // 3. Xóa dữ liệu theo role
        if ("CITIZEN".equalsIgnoreCase(roleCode)) {
            deleteCitizenData(userId);
        } else if ("COLLECTOR".equalsIgnoreCase(roleCode)) {
            deleteCollectorData(userId);
        }

        // 4. Xóa User
        userRepository.delete(user);

        log.info("Admin {} đã hard-delete tài khoản user id={} (role={})", adminEmail, userId, roleCode);
    }

    // ─────────────────────────────────────────────
    // Private helpers — Cascading delete
    // ─────────────────────────────────────────────

    /**
     * Xóa toàn bộ dữ liệu liên quan đến Citizen (con → cha).
     */
    private void deleteCitizenData(Integer userId) {
        Optional<Citizen> citizenOpt = citizenRepository.findByUserId(userId);
        if (citizenOpt.isEmpty())
            return;

        Integer citizenId = citizenOpt.get().getId();

        // Xóa các bảng tham chiếu Citizen
        voucherRedemptionRepository.deleteByCitizen_Id(citizenId);
        pointTransactionRepository.deleteByCitizenId(citizenId);
        leaderboardRepository.deleteByCitizenId(citizenId);
        feedbackRepository.deleteByCitizenId(citizenId);

        // Xóa các bảng con phụ thuộc CollectionRequest của citizen
        collectorReportImageRepository.deleteByCollectorReport_CollectionRequest_Report_Citizen_Id(citizenId);
        collectorReportItemRepository.deleteByCollectorReport_CollectionRequest_Report_Citizen_Id(citizenId);
        collectorFeedbackRepository.deleteByCollectionRequest_Report_Citizen_Id(citizenId);
        collectorReportRepository.deleteByCollectionRequest_Report_Citizen_Id(citizenId);
        collectionTrackingRepository.deleteByCollectionRequest_Report_Citizen_Id(citizenId);
        collectionRequestRepository.deleteByReport_Citizen_Id(citizenId);

        // Xóa con của WasteReport rồi xóa WasteReport
        reportImageRepository.deleteByReport_Citizen_Id(citizenId);
        wasteReportItemRepository.deleteByReport_Citizen_Id(citizenId);
        wasteReportRepository.deleteByCitizen_Id(citizenId);

        // Xóa Citizen
        citizenRepository.deleteByUserId(userId);
    }

    /**
     * Xóa toàn bộ dữ liệu liên quan đến Collector (con → cha).
     */
    private void deleteCollectorData(Integer userId) {
        Optional<Collector> collectorOpt = collectorRepository.findByUserId(userId);
        if (collectorOpt.isEmpty())
            return;

        Integer collectorId = collectorOpt.get().getId();

        // Xóa con của CollectorReport trước
        collectorReportImageRepository.deleteByCollectorReport_Collector_Id(collectorId);
        collectorReportItemRepository.deleteByCollectorReport_Collector_Id(collectorId);

        // Xóa CollectorReport
        collectorReportRepository.deleteByCollector_Id(collectorId);

        // Xóa CollectorFeedback
        collectorFeedbackRepository.deleteByCollector_Id(collectorId);

        // Xóa CollectionTracking
        collectionTrackingRepository.deleteByCollector_Id(collectorId);

        // Xóa CollectionRequest
        collectionRequestRepository.deleteByCollector_Id(collectorId);

        // Xóa Collector
        collectorRepository.deleteByUserId(userId);
    }

    // ─────────────────────────────────────────────
    // Private helpers — Validation
    // ─────────────────────────────────────────────

    /**
     * Guard chung cho preview + hard-delete:
     * - Admin không thể xóa chính mình
        * - Admin không thể xóa tài khoản ADMIN / ENTERPRISE
     */
    private void validateDeleteGuards(User targetUser, String adminEmail) {
        // Guard: Admin không thể tự xóa chính mình
        User adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (targetUser.getId().equals(adminUser.getId())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_SELF);
        }

        // Guard: Không thể xóa tài khoản có role ADMIN
        if (targetUser.getRole() != null && "ADMIN".equalsIgnoreCase(targetUser.getRole().getRoleCode())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ADMIN);
        }

        // Guard: Không thể xóa tài khoản có role ENTERPRISE
        if (targetUser.getRole() != null && "ENTERPRISE".equalsIgnoreCase(targetUser.getRole().getRoleCode())) {
            throw new AppException(ErrorCode.CANNOT_DELETE_ENTERPRISE);
        }
    }

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    private void validateBaseFields(String email, String password, String fullName) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống");
        }
    }

    private void validateRoleForProfileUpdate(User user, String expectedRoleCode) {
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        if (roleCode == null || !expectedRoleCode.equalsIgnoreCase(roleCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tài khoản không thuộc role " + expectedRoleCode);
        }
    }

    private void validateDuplicateEmailForUpdate(Integer currentUserId, String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại trong hệ hệ thống");
        }
    }

    private User createUserByRoleCode(String email, String password, String fullName, String phone, String roleCode) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại trong hệ hệ thống");
        }

        Role role = roleRepository.findByRoleCodeIgnoreCase(roleCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quyền (Role) không tồn tại"));

        User user = new User();
        user.setEmail(email.trim());
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName.trim());
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus("active");
        return userRepository.save(user);
    }

    /**
     * Chuyển đổi User entity → AdminUserResponse DTO.
     */
    private AdminUserResponse toResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roleCode(user.getRole() != null ? user.getRole().getRoleCode() : null)
                .roleName(user.getRole() != null ? user.getRole().getRoleName() : null)
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
