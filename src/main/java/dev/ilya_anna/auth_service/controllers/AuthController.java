package dev.ilya_anna.auth_service.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.ilya_anna.auth_service.dto.*;
import dev.ilya_anna.auth_service.exceptions.UserAlreadyExistsException;
import dev.ilya_anna.auth_service.exceptions.UserNotFoundException;
import dev.ilya_anna.auth_service.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication controller", 
    description = "Controller for manage authentication, " +
        "creation and deletion of users, sign in and sign out, " + 
        "change password and refresh token"
)
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

    @Operation(
        summary = "Sign in user",
        description = "Sign in user with username and password, " +
           "returns access token and refresh token" 
    )
    @PostMapping("/sign-in")
    public ResponseEntity<JwtDto> signIn(@RequestBody SignInDto signInDto) {
        try {
            return ResponseEntity.ok(signInService.signIn(signInDto));
        } catch (AuthenticationException|UserNotFoundException e) {
            log.error("user {} failed to sign in", signInDto.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } 

    }

    @Operation(
        summary = "Sign up user",
        description = "Sign up user with username, password and user personal data, " +
           "returns access token and refresh token" 
    )
    @PostMapping("/sign-up")
    public ResponseEntity<JwtDto> signUp(@RequestBody SignUpDto signUpDto) {
        try {
            return ResponseEntity.ok(signUpService.signUp(signUpDto));
        } catch (UserAlreadyExistsException e) {
            log.error("user {} failed to sign up", signUpDto.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(
        summary = "Sign out user",
        description = "Sign out user with username, " +
           "add current jwt tokens to sign out list" 
    )
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

    @Operation(
        summary = "Change user password",
        description = "Change user password with old and new password " + 
           "add current jwt tokens to sign out list" 
    )
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<Void> changePassword(@PathVariable @Parameter(description = "User id (UUID)") String userId,
                                               @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            changePasswordService.changePassword(userId, changePasswordDto);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            log.error("user {} not found", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(
        summary = "Refresh user tokens",
        description = "Refresh jwt tokens with refresh token, " +
           "returns access token and refresh token" 
    )
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
