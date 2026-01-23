package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.RegisterResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.UserDto;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registering new user with email: {}", request.getEmail());
        UserDto newUser = userService.register(request);
        logger.info("User registered successfully: {}", newUser.getId());
        return new ResponseEntity<>(new RegisterResponse("Registration successful", newUser), HttpStatus.CREATED);
    }
}
