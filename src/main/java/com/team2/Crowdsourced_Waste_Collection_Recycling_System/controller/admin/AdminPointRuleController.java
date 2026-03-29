package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.admin;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.common.ApiResponses;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.PointRuleRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.PointRuleResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PointRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/point-rule")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin – Point Rule", description = "Cấu hình quy tắc tính điểm thưởng")
public class AdminPointRuleController {

    private final PointRuleService pointRuleService;

    @GetMapping
    @Operation(summary = "Xem quy tắc tính điểm hiện tại")
    public ResponseEntity<ApiResponse<PointRuleResponse>> getPointRule() {
        return ApiResponses.okEntity(pointRuleService.getPointRule(), "Lấy quy tắc tính điểm thành công");
    }

    @PutMapping
    @Operation(summary = "Cập nhật quy tắc tính điểm")
    public ResponseEntity<ApiResponse<PointRuleResponse>> updatePointRule(
            @Valid @RequestBody PointRuleRequest request) {
        return ApiResponses.okEntity(pointRuleService.updatePointRule(request), "Cập nhật quy tắc tính điểm thành công");
    }
}
