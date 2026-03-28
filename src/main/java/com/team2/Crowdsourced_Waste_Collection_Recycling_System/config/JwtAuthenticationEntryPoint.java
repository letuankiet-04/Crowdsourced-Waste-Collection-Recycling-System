package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * EntryPoint cho lỗi unauthenticated (401).
 *
 * Khi request đi vào endpoint cần xác thực nhưng:
 * - không có token, hoặc
 * - token không hợp lệ/không decode được
 *
 * Spring Security sẽ gọi lớp này để trả về response JSON theo format ApiResponse của dự án.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");
        boolean hasBearer = authHeader != null && authHeader.startsWith("Bearer ");
        if (log.isDebugEnabled()) {
            log.debug("Unauthenticated request: uri={}, hasAuthorizationBearer={}", request.getRequestURI(), hasBearer);
        }
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;

        // Trả về đúng HTTP status và content-type JSON để frontend dễ xử lý
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
