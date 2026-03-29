package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public final class ApiResponses {
    private ApiResponses() {
    }

    public static <T> ApiResponse<T> ok(T result) {
        return ApiResponse.<T>builder().result(result).build();
    }

    public static <T> ApiResponse<T> ok(T result, String message) {
        return ApiResponse.<T>builder().result(result).message(message).build();
    }

    public static ApiResponse<Void> message(String message) {
        return ApiResponse.<Void>builder().message(message).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> okEntity(T result, String message) {
        return ResponseEntity.ok(ok(result, message));
    }
}
