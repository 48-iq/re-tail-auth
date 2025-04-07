package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignUpDto;
import dev.ilya_anna.auth_service.entities.User;

public interface SignUpService {
    JwtDto signUp(SignUpDto signUpDto);
}
