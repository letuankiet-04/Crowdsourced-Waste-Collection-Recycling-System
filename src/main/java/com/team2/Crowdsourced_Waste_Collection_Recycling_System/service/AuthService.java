package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.LoginRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AuthResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.mapper.UserMapper;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CitizenRepository citizenRepository;
    
    @Autowired
    private RecyclingEnterpriseRepository enterpriseRepository;

    @Autowired
    private CollectorRepository collectorRepository;

    @Autowired
    private EnterpriseAdminRepository enterpriseAdminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserMapper userMapper;

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already in use - {}", request.getEmail());
            throw new RuntimeException("Email already in use");
        }

        // Always default to CITIZEN for public registration
        String roleCode = "CITIZEN";
        
        Role role = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> {
                    log.error("Registration failed: Role not found - {}", "CITIZEN");
                    return new RuntimeException("Role not found: CITIZEN");
                });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(role);
        user.setStatus("active");

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        AuthResponse response = userMapper.toAuthResponse(savedUser);
        response.setMessage("User registered successfully");

        // Create Citizen profile
        Citizen citizen = new Citizen();
        citizen.setUser(savedUser);
        citizen.setAddress(request.getAddress());
        citizen.setWard(request.getWard());
        citizen.setDistrict(request.getDistrict());
        citizen.setCity(request.getCity());
        citizenRepository.save(citizen);
        
        response.setAddress(citizen.getAddress());
        response.setWard(citizen.getWard());
        response.setDistrict(citizen.getDistrict());
        response.setCity(citizen.getCity());
        
        response.setToken(jwtService.generateToken(savedUser.getEmail()));
        log.info("Citizen profile created for user ID: {}", savedUser.getId());

        return response;
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        AuthResponse response = userMapper.toAuthResponse(user);
        response.setToken(jwtService.generateToken(user.getEmail()));
        response.setMessage("Login successful");
        return response;
    }

    public void logout() {
    }
}
