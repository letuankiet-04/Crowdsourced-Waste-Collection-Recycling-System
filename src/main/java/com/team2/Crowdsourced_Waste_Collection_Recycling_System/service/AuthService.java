package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.LoginRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.RegisterRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.response.AuthenResponse;

/**
 * Interface định nghĩa các nghiệp vụ xác thực người dùng.
 * Bao gồm đăng ký, đăng nhập và đăng xuất.
 */
public interface AuthService {
    
    /**
     * Đăng ký tài khoản người dùng mới.
     * @param request Thông tin đăng ký (email, password, fullName, ...).
     * @return Thông tin xác thực sau khi đăng ký thành công (token, user info).
     */
    AuthenResponse register(RegisterRequest request);
    
    /**
     * Đăng nhập vào hệ thống.
     * @param request Thông tin đăng nhập (email, password).
     * @return Thông tin xác thực sau khi đăng nhập thành công.
     */
    AuthenResponse login(LoginRequest request);
    
    /**
     * Đăng xuất khỏi hệ thống.
     * Lưu ý: Với JWT stateless, đăng xuất chủ yếu được xử lý ở phía Client (xóa token).
     */
    void logout();
}
