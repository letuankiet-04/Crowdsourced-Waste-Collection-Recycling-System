package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(VnptEkycProperties.class)
public class VnptEkycConfig {

    @Bean
    public RestTemplate vnptEkycRestTemplate(VnptEkycProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getTimeoutMs());
        requestFactory.setReadTimeout(properties.getTimeoutMs());

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.getInterceptors().add(new VnptEkycAuthHeadersInterceptor(properties));
        return restTemplate;
    }

    static class VnptEkycAuthHeadersInterceptor implements ClientHttpRequestInterceptor {
        private final VnptEkycProperties properties;

        VnptEkycAuthHeadersInterceptor(VnptEkycProperties properties) {
            this.properties = properties;
        }

        @Override
        public ClientHttpResponse intercept(org.springframework.http.HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            if (properties.getAccessToken() != null && !properties.getAccessToken().isBlank()) {
                String raw = properties.getAccessToken().trim();
                raw = raw.replaceFirst("(?i)^bearer\\s+", "");
                headers.setBearerAuth(raw);
            }
            if (properties.getTokenId() != null && !properties.getTokenId().isBlank()) {
                headers.set("Token-id", properties.getTokenId().trim());
            }
            if (properties.getTokenKey() != null && !properties.getTokenKey().isBlank()) {
                headers.set("Token-key", properties.getTokenKey().trim());
            }
            if (properties.getMacAddress() != null && !properties.getMacAddress().isBlank()) {
                headers.set("mac-address", properties.getMacAddress().trim());
            }
            if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
                headers.setContentType(MediaType.APPLICATION_JSON);
            }
            return execution.execute(request, body);
        }
    }
}
