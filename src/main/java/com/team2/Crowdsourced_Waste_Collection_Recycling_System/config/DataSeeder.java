package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.Role;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByRoleCode("CITIZEN").isEmpty()) {
                Role citizenRole = new Role();
                citizenRole.setRoleCode("CITIZEN");
                citizenRole.setRoleName("Citizen User");
                roleRepository.save(citizenRole);
                System.out.println("Role CITIZEN created");
            }

            if (roleRepository.findByRoleCode("ENTERPRISE").isEmpty()) {
                Role enterpriseRole = new Role();
                enterpriseRole.setRoleCode("ENTERPRISE");
                enterpriseRole.setRoleName("Recycling Enterprise");
                roleRepository.save(enterpriseRole);
                System.out.println("Role ENTERPRISE created");
            }

            if (roleRepository.findByRoleCode("COLLECTOR").isEmpty()) {
                Role collectorRole = new Role();
                collectorRole.setRoleCode("COLLECTOR");
                collectorRole.setRoleName("Waste Collector");
                roleRepository.save(collectorRole);
                System.out.println("Role COLLECTOR created");
            }
            
            if (roleRepository.findByRoleCode("ENTERPRISE_ADMIN").isEmpty()) {
                Role entAdminRole = new Role();
                entAdminRole.setRoleCode("ENTERPRISE_ADMIN");
                entAdminRole.setRoleName("Enterprise Administrator");
                roleRepository.save(entAdminRole);
                System.out.println("Role ENTERPRISE_ADMIN created");
            }

            if (roleRepository.findByRoleCode("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setRoleCode("ADMIN");
                adminRole.setRoleName("System Admin");
                roleRepository.save(adminRole);
                System.out.println("Role ADMIN created");
            }
        };
    }
}
