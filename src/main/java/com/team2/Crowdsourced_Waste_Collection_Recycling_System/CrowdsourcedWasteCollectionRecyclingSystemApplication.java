package com.team2.Crowdsourced_Waste_Collection_Recycling_System;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CrowdsourcedWasteCollectionRecyclingSystemApplication {

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static void main(String[] args) {
		SpringApplication.run(CrowdsourcedWasteCollectionRecyclingSystemApplication.class, args);
	}

}
