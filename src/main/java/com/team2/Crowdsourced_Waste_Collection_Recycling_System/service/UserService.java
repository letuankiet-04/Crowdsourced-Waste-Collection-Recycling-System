package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.UserDto;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.*;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CitizenRepository citizenRepository;
    private final RecyclingEnterpriseRepository enterpriseRepository;
    private final EnterpriseAdminRepository enterpriseAdminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        String roleCode = request.getRole() != null ? request.getRole().toUpperCase() : "CITIZEN";
        Role userRole = roleRepository.findByRoleCode(roleCode)
                .orElseThrow(() -> new RuntimeException("Role " + roleCode + " not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(userRole);
        user.setStatus("active");

        User savedUser = userRepository.save(user);

        if ("CITIZEN".equals(roleCode)) {
            Citizen citizen = new Citizen();
            citizen.setUser(savedUser);
            citizen.setAddress(request.getAddress());
            citizen.setWard(request.getWard());
            citizen.setDistrict(request.getDistrict());
            citizen.setCity(request.getCity());
            citizenRepository.save(citizen);
        } else if ("ENTERPRISE".equals(roleCode)) {
            // Validate enterprise fields
            if (request.getEnterpriseName() == null || request.getEnterpriseName().isEmpty()) {
                throw new IllegalArgumentException("Enterprise Name is required for Enterprise registration");
            }

            // Create RecyclingEnterprise
            RecyclingEnterprise enterprise = new RecyclingEnterprise();
            enterprise.setName(request.getEnterpriseName());
            enterprise.setAddress(request.getAddress()); // Use same address for simplicity or add specific fields
            enterprise.setPhone(request.getPhone());
            enterprise.setEmail(request.getEmail());
            enterprise.setTaxCode(request.getTaxCode());
            enterprise.setLicenseNumber(request.getLicenseNumber());
            
            RecyclingEnterprise savedEnterprise = enterpriseRepository.save(enterprise);

            // Create EnterpriseAdmin
            EnterpriseAdmin admin = new EnterpriseAdmin();
            admin.setUser(savedUser);
            admin.setEnterprise(savedEnterprise);
            admin.setPosition("Owner"); // Default position
            admin.setIsOwner(true); // First registrant is owner
            
            enterpriseAdminRepository.save(admin);
        }

        return mapToDto(savedUser);
    }

    private UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRoleCode(user.getRole().getRoleCode());
        dto.setPasswordHash(user.getPasswordHash());
        return dto;
    }
}
