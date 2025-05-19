package dev.ilya_anna.auth_service.validators;

import dev.ilya_anna.auth_service.dto.ChangePasswordDto;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.security.DaoUserDetails;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@Slf4j
public class DaoChangePasswordDtoValidator implements Validator {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return ChangePasswordDto.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        ChangePasswordDto changePasswordDto = (ChangePasswordDto) target;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DaoUserDetails daoUserDetails = (DaoUserDetails) authentication.getPrincipal();
        User user = daoUserDetails.getUser();
        log.info("mark -----");
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            log.error("wrong old password");
            errors.rejectValue("oldPassword", "wrong.password", "wrong old password");
        }
    }
}
