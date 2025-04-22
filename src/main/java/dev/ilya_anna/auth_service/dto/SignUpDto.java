package dev.ilya_anna.auth_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    @Pattern(regexp = "^\\w{3,32}$")
    private String username;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
    private String password;
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    private String email;
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$")
    private String phone;
    @Pattern(regexp = "^[a-zA-Z0-9.-]{1,32}$")
    private String nickname;
    @Pattern(regexp = "^[a-zA-Z0-9.-]{1,64}$")
    private String name;
    @Pattern(regexp = "^[a-zA-Z0-9.-]{1,64}$")
    private String surname;
    @NotEmpty
    private List<@Pattern(regexp = "^ADMIN|USER$") String> roles;
}
