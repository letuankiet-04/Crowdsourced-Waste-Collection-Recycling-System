package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCitizenAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateCollectorAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.AdminCreateEnterpriseAccountRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AdminUserResponse;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.DeleteUserPreviewResponse;

import java.util.List;

/**
 * Các nghiệp vụ Admin – Quản lý tài khoản trong hệ thống.
 */
public interface AdminAccountService {

    /**
     * Admin tạo tài khoản CITIZEN.
     */
    AdminUserResponse createCitizenAccount(AdminCreateCitizenAccountRequest request, String adminEmail);

    /**
     * Admin tạo tài khoản COLLECTOR.
     */
    AdminUserResponse createCollectorAccount(AdminCreateCollectorAccountRequest request, String adminEmail);

    /**
     * Admin tạo tài khoản ENTERPRISE.
     */
    AdminUserResponse createEnterpriseAccount(AdminCreateEnterpriseAccountRequest request, String adminEmail);

    /**
     * Lấy toàn bộ danh sách tài khoản.
     *
     * @param status   lọc theo trạng thái ("active" | "suspended"), null = tất cả
     * @param roleCode lọc theo role (vd. "COLLECTOR"), null = tất cả
     * @param adminEmail email của admin đang đăng nhập để loại trừ khỏi danh sách
     */
    List<AdminUserResponse> getAllUsers(String status, String roleCode, String adminEmail);

    /**
     * Xem chi tiết một tài khoản.
     */
    AdminUserResponse getUserDetail(Integer userId);

    /**
     * Khóa tài khoản (status → "suspended").
     *
     * @param userId     ID tài khoản cần khóa
     * @param adminEmail email của admin đang thực hiện (từ JWT sub, để guard tự
     *                   khóa chính mình)
     */
    AdminUserResponse suspendUser(Integer userId, String adminEmail);

    /**
     * Mở khóa tài khoản (status → "active").
     *
     * @param userId     ID tài khoản cần mở
     * @param adminEmail email của admin đang thực hiện
     */
    AdminUserResponse activateUser(Integer userId, String adminEmail);

    /**
     * Preview data liên quan sẽ bị xóa khi hard-delete tài khoản.
     * Admin không thể preview xóa chính mình hoặc tài khoản ADMIN khác.
     *
     * @param userId     ID tài khoản cần xem preview
     * @param adminEmail email của admin đang thực hiện
     */
    DeleteUserPreviewResponse previewDeleteUser(Integer userId, String adminEmail);

    /**
     * Hard-delete tài khoản: xóa vĩnh viễn user và toàn bộ dữ liệu liên quan.
     * Admin không thể xóa chính mình hoặc tài khoản ADMIN khác.
     *
     * @param userId     ID tài khoản cần xóa
     * @param adminEmail email của admin đang thực hiện
     */
    void hardDeleteUser(Integer userId, String adminEmail);
}
