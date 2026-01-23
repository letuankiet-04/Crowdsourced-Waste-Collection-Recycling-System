package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        if (roleRepository.findByRoleCode("CITIZEN").isEmpty()) {
            Role citizenRole = new Role();
            citizenRole.setRoleCode("CITIZEN");
            citizenRole.setRoleName("Citizen");
            citizenRole.setDescription("Regular user who can request waste collection");
            citizenRole.setIsActive(true);
            roleRepository.save(citizenRole);
        }

        if (roleRepository.findByRoleCode("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setRoleCode("ADMIN");
            adminRole.setRoleName("Administrator");
            adminRole.setDescription("System administrator");
            adminRole.setIsActive(true);
            roleRepository.save(adminRole);
        }
        
        if (roleRepository.findByRoleCode("COLLECTOR").isEmpty()) {
            Role collectorRole = new Role();
            collectorRole.setRoleCode("COLLECTOR");
            collectorRole.setRoleName("Waste Collector");
            collectorRole.setDescription("User who collects waste");
            collectorRole.setIsActive(true);
            roleRepository.save(collectorRole);
        }

        if (roleRepository.findByRoleCode("ENTERPRISE").isEmpty()) {
            Role enterpriseRole = new Role();
            enterpriseRole.setRoleCode("ENTERPRISE");
            enterpriseRole.setRoleName("Recycling Enterprise");
            enterpriseRole.setDescription("Representative of a recycling enterprise");
            enterpriseRole.setIsActive(true);
            roleRepository.save(enterpriseRole);
        }
    }
}
