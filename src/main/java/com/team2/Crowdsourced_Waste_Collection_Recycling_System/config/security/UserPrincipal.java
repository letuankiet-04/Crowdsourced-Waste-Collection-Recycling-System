package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config.security;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserPrincipal implements UserDetails {

    private final Integer id;
    private final String email;
    private final String passwordHash;
    private final String roleCode;
    private final String status;
    private final List<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        Objects.requireNonNull(user, "user");
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.roleCode = user.getRole() != null ? user.getRole().getRoleCode() : null;
        this.status = user.getStatus();
        this.authorities = roleCode == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + roleCode));
    }

    public Integer getId() {
        return id;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == null || status.equalsIgnoreCase("active");
    }
}
