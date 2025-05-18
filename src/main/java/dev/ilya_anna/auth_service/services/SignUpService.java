package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignUpDto;
import dev.ilya_anna.auth_service.exceptions.UserAlreadyExistsException;
import jakarta.validation.Valid;

public interface SignUpService {
    JwtDto signUp(@Valid SignUpDto signUpDto) throws UserAlreadyExistsException;
}
