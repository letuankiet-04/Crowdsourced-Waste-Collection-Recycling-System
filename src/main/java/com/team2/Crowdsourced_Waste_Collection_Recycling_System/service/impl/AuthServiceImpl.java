package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AuthenticationResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.IntrospectResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.AppException;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RolePermissionRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.AuthService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.security.TokenDenylistService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.util.JWTHelper;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
/**
 * Hiện thực các nghiệp vụ xác thực/ủy quyền dựa trên JWT (Nimbus JOSE + JWT).
 *
 * Cách hoạt động tổng quát:
 * - Đăng nhập: kiểm tra email/password, sau đó phát hành JWT (HS512) chứa claim
 * "scope".
 * - Logout/Refresh: thu hồi token cũ bằng cách lưu jti vào bảng
 * invalidated_tokens.
 * - Introspect: kiểm tra token hợp lệ + chưa bị thu hồi (CustomJwtDecoder gọi
 * để quyết định cho phép truy cập).
 */
public class AuthServiceImpl implements AuthService {
    static final String ROLE_CITIZEN = "CITIZEN";
    static final String ROLE_COLLECTOR = "COLLECTOR";
    static final String ROLE_ENTERPRISE = "ENTERPRISE";
    static final String ROLE_ENTERPRISE_ADMIN = "ENTERPRISE_ADMIN";

    static final String STATUS_ACTIVE = "active";
    static final String STATUS_SUSPENDED = "suspended";

    UserRepository userRepository;
    RoleRepository roleRepository;
    RolePermissionRepository rolePermissionRepository;
    CitizenRepository citizenRepository;
    CollectorRepository collectorRepository;
    EnterpriseRepository enterpriseRepository;
    TokenDenylistService tokenDenylistService;
    PasswordEncoder passwordEncoder;
    JWTHelper jwtHelper;

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu dữ liệu đăng ký");
        }

        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại trong hệ thống");
        }

        Role role = roleRepository.findByRoleCodeIgnoreCase(ROLE_CITIZEN)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setRoleCode(ROLE_CITIZEN);
                    r.setRoleName("Citizen");
                    return roleRepository.save(r);
                });

        if (role.getRoleCode() == null || !ROLE_CITIZEN.equalsIgnoreCase(role.getRoleCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cấu hình role đăng ký không hợp lệ");
        }

        String fullName = request.getFullName().trim();
        String phone = request.getPhone() != null ? request.getPhone().trim() : null;

        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setFullName(fullName);
        u.setPhone(phone);
        u.setRole(role);
        u.setStatus(STATUS_ACTIVE);

        User savedUser;
        try {
            savedUser = userRepository.save(u);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email đã tồn tại trong hệ thống");
        }

        Citizen citizen = new Citizen();
        citizen.setUser(savedUser);
        citizen.setEmail(savedUser.getEmail());
        citizen.setFullName(savedUser.getFullName());
        citizen.setPhone(savedUser.getPhone());
        citizen.setTotalPoints(0);
        citizen.setTotalReports(0);
        citizen.setValidReports(0);
        Citizen savedCitizen = citizenRepository.save(citizen);

        Integer citizenId = savedCitizen.getId();
        String scope = buildScope(role);
        var token = jwtHelper.issueToken(savedUser, citizenId, null, null, scope);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .citizenId(citizenId)
                .collectorId(null)
                .enterpriseId(null)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse login(AuthenticationRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu dữ liệu đăng nhập");
        }
        String email = request.getEmail() != null ? request.getEmail().trim() : null;
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }

        log.info("Bắt đầu xử lý đăng nhập cho email: {}", email);
        long start = System.currentTimeMillis();

        var user = userRepository
                .findOneWithRoleByEmailIgnoreCase(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        
        log.debug("Tìm thấy user trong {} ms", System.currentTimeMillis() - start);
        long mark = System.currentTimeMillis();

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        log.debug("Xác thực mật khẩu trong {} ms", System.currentTimeMillis() - mark);
        mark = System.currentTimeMillis();

        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (STATUS_SUSPENDED.equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_SUSPENDED);
        }

        if ("deleted".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        Integer citizenId = resolveCitizenId(user);
        Integer collectorId = null;
        Integer enterpriseId = null;

        if (user.getRole() != null && user.getRole().getRoleCode() != null && user.getId() != null) {
            String roleCode = user.getRole().getRoleCode();
            if (ROLE_COLLECTOR.equalsIgnoreCase(roleCode)) {
                Collector collector = collectorRepository.findByUserId(user.getId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Tài khoản COLLECTOR thiếu hồ sơ collector"));

                // Cập nhật trạng thái thành ONLINE khi đăng nhập (nếu không bị suspended)
                if (collector.getStatus() != com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus.SUSPEND) {
                    collector.setStatus(com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus.ONLINE);
                }

                collectorId = collector.getId();
                if (collector.getEnterprise() == null || collector.getEnterprise().getId() == null) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Collector thiếu enterprise");
                }
                enterpriseId = collector.getEnterprise().getId();
            } else if (ROLE_ENTERPRISE.equalsIgnoreCase(roleCode) || ROLE_ENTERPRISE_ADMIN.equalsIgnoreCase(roleCode)) {
                Enterprise enterprise = user.getEnterprise();
                if (enterprise == null && user.getEmail() != null && !user.getEmail().isBlank()) {
                    enterprise = enterpriseRepository.findByEmailIgnoreCase(user.getEmail()).orElse(null);
                    if (enterprise != null) {
                        user.setEnterprise(enterprise);
                    }
                }

                if (enterprise == null || enterprise.getId() == null) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Tài khoản ENTERPRISE thiếu enterprise");
                }
                enterpriseId = enterprise.getId();
            }
        }
        
        log.debug("Xử lý role/id phụ trong {} ms", System.currentTimeMillis() - mark);
        mark = System.currentTimeMillis();

        String scope = buildScope(user.getRole());
        var token = jwtHelper.issueToken(user, citizenId, collectorId, enterpriseId, scope);
        
        log.info("Hoàn tất đăng nhập cho email: {} trong tổng cộng {} ms", email, System.currentTimeMillis() - start);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .citizenId(citizenId)
                .collectorId(collectorId)
                .enterpriseId(enterpriseId)
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            if (request == null || request.getToken() == null || request.getToken().isBlank()) {
                SecurityContextHolder.clearContext();
                return;
            }
            var signToken = jwtHelper.verifyToken(request.getToken());

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            tokenDenylistService.invalidate(jit, expiryTime != null ? expiryTime.toInstant() : null);
        } catch (Exception ex) {
            log.warn("Logout failed", ex);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        if (request == null || request.getToken() == null || request.getToken().isBlank()) {
            return IntrospectResponse.builder().valid(false).build();
        }

        var token = request.getToken().trim();
        boolean isValid = true;

        try {
            jwtHelper.verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    private Integer resolveCitizenId(User user) {
        if (user.getRole() == null || user.getRole().getRoleCode() == null) {
            return null;
        }
        if (!ROLE_CITIZEN.equalsIgnoreCase(user.getRole().getRoleCode())) {
            return null;
        }
        if (user.getId() == null) {
            return null;
        }
        return citizenRepository.findByUserId(user.getId()).map(Citizen::getId).orElse(null);
    }

    private String buildScope(Role role) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (role == null || role.getRoleCode() == null) {
            return stringJoiner.toString();
        }

        stringJoiner.add("ROLE_" + role.getRoleCode().toUpperCase());

        if (role.getId() == null) {
            return stringJoiner.toString();
        }

        var permissionCodes = rolePermissionRepository.findPermissionCodesByRoleId(role.getId());
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return stringJoiner.toString();
        }
        permissionCodes.stream()
                .filter(code -> code != null && !code.isBlank())
                .forEach(stringJoiner::add);

        return stringJoiner.toString();
    }
}
