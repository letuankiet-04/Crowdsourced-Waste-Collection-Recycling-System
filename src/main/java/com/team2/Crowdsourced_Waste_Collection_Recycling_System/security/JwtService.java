package com.team2.Crowdsourced_Waste_Collection_Recycling_System.security;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;


public interface JwtService {
    
    /**
     * Tạo Access Token cơ bản và tự động thêm quyền (roles) vào claim "scope".
     * @param userDetails Thông tin người dùng.
     * @return Chuỗi JWT Access Token.
     */
    String generateToken(UserDetails userDetails);
    
    /**
     * Tạo Access Token với các claims tùy chỉnh.
     * @param user Thông tin người dùng.
     * @param claims Map chứa các thông tin bổ sung.
     * @return Chuỗi JWT Access Token.
     */
    String generateToken(UserDetails user, Map<String, Object> claims);
    
    /**
     * Tạo Refresh Token (thời hạn dài hơn, dùng để cấp lại Access Token).
     * @param userDetails Thông tin người dùng.
     * @return Chuỗi JWT Refresh Token.
     */
    String generateRefreshToken(UserDetails userDetails);
    
    /**
     * Trích xuất email từ token (dùng cho các xử lý thủ công ngoài Filter).
     */
    String extractUsername(String token);
    
    /**
     * Kiểm tra tính hợp lệ của token thủ công.
     */
    boolean isTokenValid(String token, UserDetails user);
    
    /**
     * Lấy thời gian hết hạn của Access Token đã cấu hình.
     */
    long getExpirationMs();
}
