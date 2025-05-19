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
@Schema(description = "Change password dto", name = "ChangePasswordDto")
public class ChangePasswordDto {
    @Schema(description = "New password (should contain at least one uppercase letter, " + 
        "one lowercase letter, one number and one special character)", example = "asdfASDF1234!@#")
    @Pattern(regexp = "^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])|(?=.{8,})(?=.*\\d)(?=.*[!@#$%^&])|(?=.{8,})(?=.*[a-zA-Z])(?=.*[!@#$%^&]).*$")
    private String newPassword;
    @Schema(description = "Old password (actual account password)", example = "asdfASDF1234!@#")
    @NotBlank
    private String oldPassword;
}
