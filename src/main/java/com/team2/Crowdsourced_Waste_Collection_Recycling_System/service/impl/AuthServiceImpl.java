package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.security.JwtService;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.security.UserPrincipal;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.LoginRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AuthenResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.mapper.UserMapper;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Triển khai các nghiệp vụ xác thực.
 * Sử dụng Spring Security AuthenticationManager để xác thực và JwtService để tạo token.
 */
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * Xử lý đăng ký người dùng mới.
     * 1. Kiểm tra tính hợp lệ của thông tin đầu vào.
     * 2. Kiểm tra email đã tồn tại chưa.
     * 3. Mã hóa mật khẩu và lưu người dùng vào database.
     * 4. Tạo bộ token (Access & Refresh) trả về cho client.
     */
    @Override
    public AuthenResponse register(RegisterRequest request) {
        String email = request.getEmail();
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

        // Mặc định gán role CITIZEN nếu không chỉ định
        String roleCode = (request.getRoleCode() == null || request.getRoleCode().isBlank()) ? "CITIZEN" : request.getRoleCode();
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quyền (Role) không tồn tại"));

        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setFullName(request.getFullName());
        u.setPhone(request.getPhone());
        u.setRole(role);
        u.setStatus("active");
        userRepository.save(u);

        UserPrincipal userPrincipal = new UserPrincipal(u);
        String token = jwtService.generateToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);
        
        return new AuthenResponse(token, refreshToken, "Bearer", jwtService.getExpirationMs(), userMapper.toDto(u));
    }

    /**
     * Xử lý đăng nhập.
     * 1. Sử dụng AuthenticationManager để xác thực email/password.
     * 2. Lấy thông tin người dùng từ DB.
     * 3. Tạo và trả về bộ token.
     */
    @Override
    public AuthenResponse login(LoginRequest request) {
        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không chính xác");
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Người dùng không tồn tại"));

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String token = jwtService.generateToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return new AuthenResponse(token, refreshToken, "Bearer", jwtService.getExpirationMs(), userMapper.toDto(user));
    }

    /**
     * Xử lý đăng xuất.
     * Xóa thông tin xác thực trong SecurityContext.
     */
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
