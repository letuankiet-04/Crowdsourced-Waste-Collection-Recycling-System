package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Citizen;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Collector;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Enterprise;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.enums.CollectorStatus;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.RoleRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.collector.CollectorRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.enterprise.EnterpriseRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
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
    private final CitizenRepository citizenRepository;
    private final CollectorRepository collectorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInit(
            UserRepository userRepository,
            RoleRepository roleRepository,
            EnterpriseRepository enterpriseRepository,
            CitizenRepository citizenRepository,
            CollectorRepository collectorRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.citizenRepository = citizenRepository;
        this.collectorRepository = collectorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = getOrCreateRole("ADMIN", "Admin");
        Role citizenRole = getOrCreateRole("CITIZEN", "Citizen");
        Role enterpriseRole = getOrCreateRole("ENTERPRISE", "Enterprise");
        Role collectorRole = getOrCreateRole("COLLECTOR", "Collector");

        Enterprise demoEnterprise = getOrCreateEnterprise("enterprise@gmail.com", "Demo Enterprise");
        Enterprise ecoEnterprise = getOrCreateEnterprise("enterprise2@gmail.com", "Eco Enterprise");

        LocalDateTime now = LocalDateTime.now();

        User adminUser = getOrCreateUser(
                "admin@gmail.com",
                "admin123",
                "Admin Demo",
                "0900000001",
                "active",
                adminRole,
                null,
                now
        );

        User citizenUser = getOrCreateUser(
                "citizen@gmail.com",
                "citizen123",
                "Citizen Demo",
                "0900000002",
                "active",
                citizenRole,
                null,
                now
        );

        User enterpriseUser = getOrCreateUser(
                "enterprise@gmail.com",
                "enterprise123",
                "Enterprise Admin Demo",
                "0900000003",
                "active",
                enterpriseRole,
                demoEnterprise,
                now
        );

        User collectorUser1 = getOrCreateUser(
                "collector1@gmail.com",
                "collector123",
                "Collector Demo 1",
                "0900000101",
                "active",
                collectorRole,
                null,
                now
        );

        User collectorUser2 = getOrCreateUser(
                "collector2@gmail.com",
                "collector123",
                "Collector Demo 2",
                "0900000102",
                "active",
                collectorRole,
                null,
                now
        );

        ensureCitizenProfile(citizenUser, "12 Citizen Street", "Ward 1", "Ho Chi Minh City", 120, 8, 7);
        ensureCollectorProfile(collectorUser1, demoEnterprise, "COL-001", "Truck", "51A-12345", CollectorStatus.ONLINE, now);
        ensureCollectorProfile(collectorUser2, ecoEnterprise, "COL-002", "Motorbike", "59B1-67890", CollectorStatus.OFFLINE, now);
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

    private Enterprise getOrCreateEnterprise(String email, String name) {
        return enterpriseRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Enterprise e = new Enterprise();
                    e.setName(name);
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

    private User getOrCreateUser(
            String email,
            String rawPassword,
            String fullName,
            String phone,
            String status,
            Role role,
            Enterprise enterprise,
            LocalDateTime now
    ) {
        return userRepository.findByEmail(email)
                .map(existing -> {
                    boolean changed = false;

                    if (existing.getRole() == null || (role != null && existing.getRole().getId() != null
                            && !existing.getRole().getId().equals(role.getId()))) {
                        existing.setRole(role);
                        changed = true;
                    }

                    if (enterprise != null && (existing.getEnterprise() == null || existing.getEnterprise().getId() == null
                            || !existing.getEnterprise().getId().equals(enterprise.getId()))) {
                        existing.setEnterprise(enterprise);
                        changed = true;
                    }

                    if (fullName != null && !fullName.isBlank()
                            && (existing.getFullName() == null || !existing.getFullName().equals(fullName))) {
                        existing.setFullName(fullName);
                        changed = true;
                    }

                    if (phone != null && !phone.isBlank()
                            && (existing.getPhone() == null || !existing.getPhone().equals(phone))) {
                        existing.setPhone(phone);
                        changed = true;
                    }

                    if (status != null && !status.isBlank()
                            && (existing.getStatus() == null || !existing.getStatus().equalsIgnoreCase(status))) {
                        existing.setStatus(status);
                        changed = true;
                    }

                    if (changed) {
                        existing.setUpdatedAt(now);
                        return userRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {
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
                    return userRepository.save(user);
                });
    }

    private void ensureCitizenProfile(
            User user,
            String address,
            String ward,
            String city,
            Integer totalPoints,
            Integer totalReports,
            Integer validReports
    ) {
        if (citizenRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }
        Citizen citizen = new Citizen();
        citizen.setUser(user);
        citizen.setEmail(user.getEmail());
        citizen.setFullName(user.getFullName());
        citizen.setPasswordHash(user.getPasswordHash());
        citizen.setPhone(user.getPhone());
        citizen.setAddress(address);
        citizen.setWard(ward);
        citizen.setCity(city);
        citizen.setTotalPoints(totalPoints);
        citizen.setTotalReports(totalReports);
        citizen.setValidReports(validReports);
        citizenRepository.save(citizen);
    }

    private void ensureCollectorProfile(
            User user,
            Enterprise enterprise,
            String employeeCode,
            String vehicleType,
            String vehiclePlate,
            CollectorStatus status,
            LocalDateTime now
    ) {
        if (collectorRepository.findByUserId(user.getId()).isPresent()) {
            return;
        }
        Collector collector = new Collector();
        collector.setUser(user);
        collector.setEnterprise(enterprise);
        collector.setEmail(user.getEmail());
        collector.setFullName(user.getFullName());
        collector.setEmployeeCode(employeeCode);
        collector.setVehicleType(vehicleType);
        collector.setVehiclePlate(vehiclePlate);
        collector.setStatus(status);
        collector.setLastLocationUpdate(now);
        collector.setViolationCount(0);
        collector.setCreatedAt(now);
        collectorRepository.save(collector);
    }
}
