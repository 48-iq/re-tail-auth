package dev.ilya_anna.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Sign in user with username and password", name = "SignInDto")
public class SignInDto {
    @Schema(description = "User username", example = "my_username")
    @Pattern(regexp = "^\\w{3,32}$")
    @NotBlank
    private String username;
    @Schema(description = "User password", example = "asdfASDF1234!@#")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    @NotBlank
    private String password;
}
