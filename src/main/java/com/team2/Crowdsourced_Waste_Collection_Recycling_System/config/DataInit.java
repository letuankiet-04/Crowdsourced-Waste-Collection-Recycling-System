package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class DataInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInit(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EnterpriseRepository enterpriseRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = getOrCreateRole("ADMIN", "Admin");
        Role citizenRole = getOrCreateRole("CITIZEN", "Citizen");
        Role enterpriseRole = getOrCreateRole("ENTERPRISE", "Enterprise");

        Enterprise demoEnterprise = getOrCreateEnterprise("enterprise@demo.local");

        LocalDateTime now = LocalDateTime.now();

        ensureUser(
                "admin@gmail.com",
                "admin123",
                "Admin Demo",
                "0900000001",
                "active",
                adminRole,
                null,
                now
        );

        ensureUser(
                "citizen@gmail.com",
                "citizen123",
                "Citizen Demo",
                "0900000002",
                "active",
                citizenRole,
                null,
                now
        );

        ensureUser(
                "enterprise@gmail.com",
                "enterprise123git ",
                "Enterprise Admin Demo",
                "0900000003",
                "active",
                enterpriseRole,
                demoEnterprise,
                now
        );
    }

    private Role getOrCreateRole(String roleCode, String roleName) {
        return roleRepository.findByRoleCodeIgnoreCase(roleCode)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleCode(roleCode);
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private Enterprise getOrCreateEnterprise(String email) {
        return enterpriseRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Enterprise e = new Enterprise();
                    e.setName("Demo Enterprise");
                    e.setAddress("123 Demo Street");
                    e.setPhone("0900009999");
                    e.setEmail(email);
                    e.setServiceWards("[]");
                    e.setServiceCities("[]");
                    e.setStatus("active");
                    e.setCreatedAt(now);
                    e.setUpdatedAt(now);
                    return enterpriseRepository.save(e);
                });
    }

    private void ensureUser(
            String email,
            String rawPassword,
            String fullName,
            String phone,
            String status,
            Role role,
            Enterprise enterprise,
            LocalDateTime now
    ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(role);
        user.setEnterprise(enterprise);
        user.setStatus(status);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userRepository.save(user);
    }
}
