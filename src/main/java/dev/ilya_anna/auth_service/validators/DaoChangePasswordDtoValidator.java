package dev.ilya_anna.auth_service.validators;

import dev.ilya_anna.auth_service.dto.ChangePasswordDto;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.security.DaoUserDetails;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class DaoChangePasswordDtoValidator implements Validator {

    @Override
    public boolean supports(@Nonnull Class<?> clazz) {
        return ChangePasswordDto.class.equals(clazz);
    }

    @Override
    public void validate(@Nonnull Object target, @Nonnull Errors errors) {
        ChangePasswordDto changePasswordDto = (ChangePasswordDto) target;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DaoUserDetails daoUserDetails = (DaoUserDetails) authentication.getPrincipal();
        User user = daoUserDetails.getUser();
        if (!user.getPassword().equals(changePasswordDto.getOldPassword())) {
            errors.rejectValue("oldPassword", "wrong.password", "wrong old password");
        }
    }
}
