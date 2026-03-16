package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.authentication;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.profile.CitizenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthRegisterCitizenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CitizenRepository citizenRepository;

    @Test
    void register_createsCitizenUserAndCitizenProfile() throws Exception {
        String email = "citizen.register.test@gmail.com";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "pw123456",
                                  "fullName": "Citizen Register Test",
                                  "phone": "0900000009"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk());

        var user = userRepository.findOneWithAuthByEmail(email).orElseThrow();
        assertNotNull(user.getRole());
        assertEquals("CITIZEN", user.getRole().getRoleCode());
        assertNull(user.getEnterprise());
        assertTrue(citizenRepository.findByUserId(user.getId()).isPresent());
    }
}

