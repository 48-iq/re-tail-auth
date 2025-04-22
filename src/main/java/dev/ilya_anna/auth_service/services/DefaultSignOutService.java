package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.entities.SignOutMark;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.events.UserSignOutEvent;
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

    @Override
    public void validateSignOutMark(String userId, LocalDateTime time) {
        Optional<SignOutMark> signOutMarkOptional = signOutMarkRepository.findById(userId);
        if (signOutMarkOptional.isPresent()) {
            SignOutMark signOutMark = signOutMarkOptional.get();
            LocalDateTime signOutTime = signOutMark.getSignOutTime();
            if (signOutTime.isAfter(time)) {
                throw new IllegalArgumentException("authentication expired");
            }
        }
    }

    @Override
    public void signOut(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("User with username " + username + " not found")
        );

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
