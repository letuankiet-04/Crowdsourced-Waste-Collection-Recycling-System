package com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.impl;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.dto.request.ChangePasswordRequest;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.AppException;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.exception.ErrorCode;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.repository.authentication.UserRepository;
import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Thiếu token");
        }
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thiếu dữ liệu");
        }

        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu hiện tại không được để trống");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu mới không được để trống");
        }
        if (newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải có ít nhất 6 ký tự");
        }
        if (confirmNewPassword != null && !confirmNewPassword.isBlank() && !newPassword.equals(confirmNewPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Xác nhận mật khẩu không khớp");
        }

        var user = userRepository.findOneWithRoleByEmailIgnoreCase(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Chặn user đã bị soft-delete (JWT có thể vẫn còn hiệu lực)
        if ("deleted".equalsIgnoreCase(user.getStatus())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tài khoản chưa có mật khẩu");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mật khẩu hiện tại không đúng");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}

