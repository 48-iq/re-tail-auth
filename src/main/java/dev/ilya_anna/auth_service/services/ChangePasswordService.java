package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.ChangePasswordDto;
import jakarta.validation.Valid;

public interface ChangePasswordService {
    void changePassword(String userId, @Valid ChangePasswordDto changePasswordDto);
}
