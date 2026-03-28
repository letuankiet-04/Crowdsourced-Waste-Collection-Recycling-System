package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "vnpt.ekyc")
public class VnptEkycProperties {
    private String baseUrl = "https://api.idg.vnpt.vn";
    private String accessToken;
    private String tokenId;
    private String tokenKey;
    private String macAddress = "TEST1";
    private int timeoutMs = 15000;
}
