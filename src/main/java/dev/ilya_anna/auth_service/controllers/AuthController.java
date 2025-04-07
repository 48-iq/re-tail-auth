package dev.ilya_anna.auth_service.controllers;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignInDto;
import dev.ilya_anna.auth_service.dto.SignUpDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(SignInDto signInDto) {
        return null;

    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtDto> signUp(SignUpDto signUpDto) {
        return null;
    }
}
