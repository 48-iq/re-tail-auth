package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignInDto;
import org.springframework.stereotype.Service;

@Service
public class DaoSignInService implements SignInService{

    @Override
    public JwtDto signIn(SignInDto signInDto) {
        return null;
    }
}
