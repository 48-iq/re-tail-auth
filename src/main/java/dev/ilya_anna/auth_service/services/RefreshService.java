package dev.ilya_anna.auth_service.services;


import dev.ilya_anna.auth_service.dto.JwtDto;

public interface RefreshService {
    JwtDto refresh(String refreshToken);
}
