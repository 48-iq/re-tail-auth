package dev.ilya_anna.auth_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User registration data (username, password, user personal data)", name = "SignUpDto")
public class SignUpDto {
    @Schema(description = "User username", example = "my_username")
    @Pattern(regexp = "^\\w{3,32}$")
    private String username;

    @Schema(description = "User password", example = "asdfASDF1234!@#")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    private String password;

    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @Schema(description = "User email", example = "Q2t5t@example.com")
    private String email;

    @Schema(description = "User phone number", example = "+380501234567")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$")
    private String phone;

    @Schema(description = "User nickname which will be shown to other users", example = "my_nickname")
    @Pattern(regexp = "^[a-zA-Z0-9.-]{1,32}$")
    private String nickname;

    @Schema(description = "User name", example = "John")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9.-]{1,64}$")
    private String name;

    @Schema(description = "User surname", example = "Doe")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я0-9.-]{1,64}$")
    private String surname;

    @Schema(description = "User roles  ADMIN|USER", example = "[\"ADMIN\", \"USER\"]")
    @NotEmpty
    private List<@Pattern(regexp = "^ADMIN|USER$") String> roles;
}
