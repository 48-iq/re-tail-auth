package dev.ilya_anna.auth_service.validators;

import dev.ilya_anna.auth_service.dto.SignUpDto;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DaoSignUpDtoValidator implements Validator {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return SignUpDto.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {

        SignUpDto signUpDto = (SignUpDto) target;
        String username = signUpDto.getUsername();

        if (userRepository.existsByUsername(username)) {
            errors.rejectValue("username", "username.exists");
        }
    }
}
