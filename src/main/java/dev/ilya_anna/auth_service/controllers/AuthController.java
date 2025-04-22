package dev.ilya_anna.auth_service.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.ilya_anna.auth_service.dto.*;
import dev.ilya_anna.auth_service.exceptions.UserAlreadyExistsException;
import dev.ilya_anna.auth_service.exceptions.UserNotFoundException;
import dev.ilya_anna.auth_service.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private SignInService signInService;

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private SignOutService signOutService;

    @Autowired
    private ChangePasswordService changePasswordService;

    @Autowired
    private RefreshService refreshService;

    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(@RequestBody SignInDto signInDto) {
        try {
            return ResponseEntity.ok(signInService.signIn(signInDto));
        } catch (AuthenticationException e) {
            log.error("user {} failed to sign in", signInDto.getUsername());
            return ResponseEntity.badRequest().build();
        } catch (UserNotFoundException e) {
            log.error("user {} not found", signInDto.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtDto> signUp(@RequestBody SignUpDto signUpDto) {
        try {
            return ResponseEntity.ok(signUpService.signUp(signUpDto));
        } catch (UserAlreadyExistsException e) {
            log.error("user {} failed to sign up", signUpDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();
            signOutService.signOut(username);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Void> changePassword(@PathVariable String userId,
                                               @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            changePasswordService.changePassword(userId, changePasswordDto);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            log.error("user {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtDto> refresh(RefreshDto refreshDto) {
        try {
            return ResponseEntity.ok(refreshService.refresh(refreshDto.getRefresh()));
        } catch (UserNotFoundException e) {
            log.error("failed to refresh {}", refreshDto.getRefresh());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (JWTVerificationException e) {
            log.error("failed to refresh {}", refreshDto.getRefresh());
            return ResponseEntity.badRequest().build();
        }
    }
}
