package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.entities.SignOutMark;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.events.UserSignOutEvent;
import dev.ilya_anna.auth_service.exceptions.SignOutMarkValidationException;
import dev.ilya_anna.auth_service.exceptions.UserNotFoundException;
import dev.ilya_anna.auth_service.producers.UserSignOutEventProducer;
import dev.ilya_anna.auth_service.repositories.SignOutMarkRepository;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class DefaultSignOutService implements SignOutService{
    @Autowired
    private UserSignOutEventProducer userSignOutEventProducer;

    @Autowired
    private SignOutMarkRepository signOutMarkRepository;

    @Autowired
    private UuidService uuidService;

    @Value("${app.jwt.refresh.duration}")
    private Long refreshDuration;

    @Autowired
    private UserRepository userRepository;

    /**
     * Validates sign out mark for given user id and time. If mark exists and it's sign out time is after given time
     * then IllegalArgumentException is thrown with message "authentication expired"
     *
     * @param userId user id
     * @param time   time to compare with sign out time
     * @throws IllegalArgumentException if authentication expired
     */
    @Override
    public void validateSignOutMark(String userId, LocalDateTime time) {
        Optional<SignOutMark> signOutMarkOptional = signOutMarkRepository.findById(userId);
        if (signOutMarkOptional.isPresent()) {

            SignOutMark signOutMark = signOutMarkOptional.get();
            LocalDateTime signOutTime = signOutMark.getSignOutTime();
            if (signOutTime.isAfter(time)) {
                throw new SignOutMarkValidationException("authentication expired");
            }
        }
    }

    /**
     * Signs out user with given username.
     * If user is already signed out, then his sign out mark is deleted and new one is created.
     * Then user sign out event is sent to broker for sign out user in other services.
     *
     * @param username user name
     * @throws UserNotFoundException if user with given username not found
     */
    @Override
    public void signOut(User user) {
        
        String userId = user.getId();
        //check if user is already signed out before
        if (signOutMarkRepository.existsById(userId)) {
            //delete mark for create new one
            signOutMarkRepository.deleteById(userId);
        }

        //create new mark with user sign out time
        SignOutMark signOutMark = SignOutMark.builder()
                .id(userId)
                .userId(userId)
                .signOutTime(LocalDateTime.now())
                .ttl(refreshDuration)
                .build();

        //save mark
        signOutMarkRepository.save(signOutMark);

        //create broker event
        UserSignOutEvent userSignOutEvent = UserSignOutEvent.builder()
                .id(uuidService.generate())
                .time(LocalDateTime.now())
                .userId(userId)
                .build();

        //send broker event
        userSignOutEventProducer.sendUserSignOutEvent(userSignOutEvent);
        log.info("User {} signed out", userId);
    }
}
