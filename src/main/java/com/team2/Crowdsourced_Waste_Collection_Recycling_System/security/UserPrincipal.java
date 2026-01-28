package com.team2.Crowdsourced_Waste_Collection_Recycling_System.security;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adapter class chuyển đổi từ User Entity sang UserDetails của Spring Security.
 * Lưu giữ thông tin người dùng và danh sách quyền (authorities).
 */
public class UserPrincipal implements UserDetails {
    private final User user;

    public UserPrincipal(User user) { 
        this.user = user; 
    }

    /**
     * Trích xuất các quyền của người dùng.
     * Prefix "ROLE_" được thêm vào để khớp với hasRole() trong SecurityConfig.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleCode = user.getRole() != null ? user.getRole().getRoleCode() : "CITIZEN";
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleCode));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override 
    public String getUsername() {
        return user.getEmail();
    }

    @Override 
    public boolean isAccountNonExpired() {
        return isActive();
    }

    @Override 
    public boolean isAccountNonLocked() {
        return isActive();
    }

    @Override 
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override 
    public boolean isEnabled() {
        return isActive();
    }

    /**
     * Kiểm tra người dùng có đang ở trạng thái hoạt động (active) không.
     */
    private boolean isActive() {
        return user.getStatus() == null || "active".equalsIgnoreCase(user.getStatus());
    }

    /**
     * Lấy thực thể User gốc nếu cần dùng thêm thông tin khác.
     */
    public User getUser() {
        return user;
    }
}
