package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.ChangePasswordDto;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import dev.ilya_anna.auth_service.validators.DaoChangePasswordDtoValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class DaoChangePasswordService implements ChangePasswordService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DaoChangePasswordDtoValidator daoChangePasswordDtoValidator;

    @Autowired
    private SignOutService signOutService;

    /**
     * Change user password with old and new password add current jwt tokens to sign out list
     *
     * @param userId        user id (UUID)
     * @param changePasswordDto contains old and new passwords
     * @throws IllegalArgumentException if user with given id not found or old password is incorrect
     */
    @Override
    public void changePassword(String userId, @Valid ChangePasswordDto changePasswordDto) {

        //validate old password
        BindingResult bindingResult = new BeanPropertyBindingResult(changePasswordDto, "changePasswordDto");
        daoChangePasswordDtoValidator.validate(changePasswordDto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.toString());
        }

        //get user and change password in database
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("user with id " + userId + " not found"));
        user.setPassword(changePasswordDto.getNewPassword());
        userRepository.save(user);

        //sign out user after changing password
        signOutService.signOut(userId);
    }
}
