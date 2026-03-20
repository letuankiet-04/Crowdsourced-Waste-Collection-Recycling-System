package com.team2.Crowdsourced_Waste_Collection_Recycling_System.controller.enterprise;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentCreateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.EnterprisePointAdjustmentUpdateRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.ApiResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.EnterprisePointAdjustmentResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.EnterprisePointAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/points/adjustments")
@RequiredArgsConstructor
@Tag(name = "Enterprise Point Adjustments", description = "Thêm/sửa/xóa giao dịch điều chỉnh điểm của enterprise")
public class EnterprisePointAdjustmentController extends EnterpriseControllerSupport {
    private final EnterprisePointAdjustmentService enterprisePointAdjustmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    @Operation(summary = "Thêm điểm", description = "Tạo giao dịch điều chỉnh điểm cho citizen trong phạm vi collectionRequest của enterprise")
    public ResponseEntity<ApiResponse<EnterprisePointAdjustmentResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EnterprisePointAdjustmentCreateRequest request) {
        Integer enterpriseId = extractEnterpriseId(jwt);
        EnterprisePointAdjustmentResponse result = enterprisePointAdjustmentService.create(enterpriseId, jwt.getSubject(), request);
        return okEntity(result, "Thêm điều chỉnh điểm thành công");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    @Operation(summary = "Sửa điểm", description = "Chỉ cho phép sửa giao dịch điều chỉnh mới nhất của citizen")
    public ResponseEntity<ApiResponse<EnterprisePointAdjustmentResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id,
            @Valid @RequestBody EnterprisePointAdjustmentUpdateRequest request) {
        Integer enterpriseId = extractEnterpriseId(jwt);
        EnterprisePointAdjustmentResponse result = enterprisePointAdjustmentService.update(enterpriseId, jwt.getSubject(), id, request);
        return okEntity(result, "Cập nhật điều chỉnh điểm thành công");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTERPRISE', 'ENTERPRISE_ADMIN')")
    @Operation(summary = "Xóa điểm", description = "Chỉ cho phép xóa giao dịch điều chỉnh mới nhất của citizen")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Integer id) {
        Integer enterpriseId = extractEnterpriseId(jwt);
        enterprisePointAdjustmentService.delete(enterpriseId, jwt.getSubject(), id);
        return okEntity(null, "Xóa điều chỉnh điểm thành công");
    }
}

