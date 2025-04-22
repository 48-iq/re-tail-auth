package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignInDto;
import org.springframework.security.core.AuthenticationException;

public interface SignInService {
    JwtDto signIn(SignInDto signInDto) throws AuthenticationException;
}
