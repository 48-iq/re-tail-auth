package dev.ilya_anna.auth_service.validators;

import dev.ilya_anna.auth_service.dto.SignInDto;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SIgnUpDtoValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(SignInDto.class);
    }

    @Override
    public void validate(@Nonnull Object target,
                         @Nonnull Errors errors) {

        SignInDto signInDto = (SignInDto) target;
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();

        if (username.length() < 3) {
            errors.reject("username", "the username is too small");
        }
        else if (username.length() > 32) {
            errors.reject("username", "the username is too long");
        }
        if (password.length() < 8) {
            errors.reject("password", "the password is too small");
        }
        else if (password.length() > 64) {
            errors.reject("password", "the password is too long");
        }
        if (password.matches(".*\\W.*")) {
            errors.reject("password", "the password must contain a special characters");
        }

    }
}
