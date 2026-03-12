package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCitizenAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCollectorAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateEnterpriseAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminUserResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.VehicleType;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.AppException;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
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

    @Override
    @Transactional
    public AdminUserResponse createCitizenAccount(AdminCreateCitizenAccountRequest request, String adminEmail) {
        validateBaseFields(request.getEmail(), request.getPassword(), request.getFullName());
        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(), request.getPhone(),
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

        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(), request.getPhone(),
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

        User savedUser = createUserByRoleCode(request.getEmail(), request.getPassword(), request.getFullName(), request.getPhone(),
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
                .filter(user -> adminEmail == null || user.getEmail() == null || !user.getEmail().equalsIgnoreCase(adminEmail))
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
        User saved = userRepository.save(user);

        log.info("Admin {} đã mở khóa tài khoản user id={}", adminEmail, userId);
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────

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
