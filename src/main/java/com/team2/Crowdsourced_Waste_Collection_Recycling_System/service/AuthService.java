package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.AuthResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        AuthResponse response = new AuthResponse();
        response.setEmail(savedUser.getEmail());
        response.setRole(role.getRoleCode());
        response.setMessage("User registered successfully");
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setPhone(savedUser.getPhone());

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
        
        log.info("Citizen profile created for user ID: {}", savedUser.getId());

        return response;
    }
}
