package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Hidden
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
            "message", "Welcome to Crowdsourced Waste Collection & Recycling System API",
            "status", "UP",
            "documentation", "/swagger-ui.html"
        );
    }
}
