package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.entities.User;

public interface JwtService {
    String generateRefresh(User user);
    String generateAccess(User user);
}
