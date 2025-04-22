package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.ChangePasswordDto;

public interface ChangePasswordService {
    void changePassword(String userId, ChangePasswordDto changePasswordDto);
}
