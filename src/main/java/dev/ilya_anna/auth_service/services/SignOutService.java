package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.exceptions.SignOutMarkValidationException;

import java.time.LocalDateTime;

public interface SignOutService {
    void signOut(User user);
    void validateSignOutMark(String userId, LocalDateTime time) throws SignOutMarkValidationException;
}
