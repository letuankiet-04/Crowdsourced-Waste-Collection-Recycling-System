package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyClassifyRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyLivenessRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrBackRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFrontRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyOcrFullRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EkcyUploadRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkcyFullFlowResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkcyUploadResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EkycSessionResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EkcyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth/ekyc")
@RequiredArgsConstructor
@Tag(name = "eKYC", description = "Tích hợp VNPT eKYC")
public class EkcyController {
    private final EkcyService ekcyService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload ảnh lên VNPT", description = "Trả về hash để dùng cho các bước eKYC tiếp theo")
    public ApiResponse<EkcyUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "enhance", required = false, defaultValue = "false") boolean enhance
    ) {
        String hash = ekcyService.upload(EkcyUploadRequest.builder().title(title).description(description).build(), file, enhance);
        return ApiResponse.<EkcyUploadResponse>builder().result(EkcyUploadResponse.builder().hash(hash).build()).build();
    }

    @PostMapping("/classify")
    @Operation(summary = "Classify document", description = "Nhận diện loại giấy tờ từ ảnh (hash)")
    public ApiResponse<JsonNode> classify(@RequestBody EkcyClassifyRequest request) {
        return ApiResponse.<JsonNode>builder().result(ekcyService.classify(request)).build();
    }

    @PostMapping("/liveness")
    @Operation(summary = "Liveness check", description = "Kiểm tra giấy tờ thật/giả")
    public ApiResponse<JsonNode> liveness(@RequestBody EkcyLivenessRequest request) {
        return ApiResponse.<JsonNode>builder().result(ekcyService.liveness(request)).build();
    }

    @PostMapping("/ocr/front")
    @Operation(summary = "OCR front", description = "OCR mặt trước giấy tờ")
    public ApiResponse<JsonNode> ocrFront(@RequestBody EkcyOcrFrontRequest request) {
        return ApiResponse.<JsonNode>builder().result(ekcyService.ocrFront(request)).build();
    }

    @PostMapping("/ocr/back")
    @Operation(summary = "OCR back", description = "OCR mặt sau giấy tờ")
    public ApiResponse<JsonNode> ocrBack(@RequestBody EkcyOcrBackRequest request) {
        return ApiResponse.<JsonNode>builder().result(ekcyService.ocrBack(request)).build();
    }

    @PostMapping("/ocr/full")
    @Operation(summary = "OCR full", description = "OCR full (front + back)")
    public ApiResponse<JsonNode> ocrFull(@RequestBody EkcyOcrFullRequest request) {
        return ApiResponse.<JsonNode>builder().result(ekcyService.ocrFull(request)).build();
    }

    @PostMapping(value = "/flow", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Full eKYC flow", description = "upload -> classify -> liveness -> OCR front/back -> OCR full")
    public ApiResponse<EkcyFullFlowResponse> flow(
            @RequestPart("front") MultipartFile front,
            @RequestPart("back") MultipartFile back,
            @RequestParam("clientSession") String clientSession,
            @RequestParam(value = "token", required = false) String token,
            @RequestParam(value = "type", required = false, defaultValue = "-1") Integer type,
            @RequestParam(value = "validatePostcode", required = false, defaultValue = "true") Boolean validatePostcode,
            @RequestParam(value = "cropParam", required = false) String cropParam,
            @RequestParam(value = "enhance", required = false, defaultValue = "false") boolean enhance
    ) {
        return ApiResponse.<EkcyFullFlowResponse>builder()
                .result(ekcyService.fullFlow(front, back, clientSession, token, type, validatePostcode, cropParam, enhance))
                .build();
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Lấy kết quả eKYC đã lưu", description = "Trả về kết quả eKYC theo sessionId")
    public ApiResponse<EkycSessionResponse> getSession(@PathVariable("id") String id) {
        return ApiResponse.<EkycSessionResponse>builder()
                .result(ekcyService.getSession(id))
                .build();
    }
}
