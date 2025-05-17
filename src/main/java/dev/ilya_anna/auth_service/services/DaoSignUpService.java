package dev.ilya_anna.auth_service.services;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.dto.SignUpDto;
import dev.ilya_anna.auth_service.entities.CreateUserTransaction;
import dev.ilya_anna.auth_service.entities.Role;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.events.UserCreatedEvent;
import dev.ilya_anna.auth_service.exceptions.UserAlreadyExistsException;
import dev.ilya_anna.auth_service.producers.UserCreatedEventsProducer;
import dev.ilya_anna.auth_service.repositories.CreateUserTransactionRepository;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import dev.ilya_anna.auth_service.validators.DaoSignUpDtoValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service
public class DaoSignUpService implements SignUpService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DaoSignUpDtoValidator daoSignUpDtoValidator;

    @Autowired
    private UuidService uuidService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCreatedEventsProducer userCreatedEventsProducer;

    @Autowired
    private CreateUserTransactionRepository createUserTransactionRepository;
    
    /**
     * Sign up user with given user data, and return jwt tokens.
     * Send user created event to broker, for create user in other services.
     * If user with given username exists, throw UserAlreadyExistsException.
     * 
     * @param signUpDto contains user data
     * @return jwt tokens for access and refresh
     * @throws UserAlreadyExistsException if user with given username already exists
     */
    @Override
    public JwtDto signUp(@Valid SignUpDto signUpDto) {

        //check if username is unique
        BindingResult errors = new BeanPropertyBindingResult(signUpDto, "signUpDto");
        daoSignUpDtoValidator.validate(signUpDto, errors);
        if (errors.hasErrors()) {
            throw  new UserAlreadyExistsException("user with username" +
                    signUpDto.getUsername() + " already exists");
        }

        //create user
        User user = User.builder()
                .id(uuidService.generate())
                .username(signUpDto.getUsername())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .roles(signUpDto.getRoles().stream().map(Role::new).toList())
                .build();

        //create broker event
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .id(user.getId())
                .userId(user.getId())
                .email(signUpDto.getEmail())
                .name(signUpDto.getName())
                .surname(signUpDto.getSurname())
                .email(signUpDto.getEmail())
                .phone(signUpDto.getPhone())
                .build();

        //create entity for outbox pattern
        CreateUserTransaction createUserTransaction = CreateUserTransaction.builder()
                .id(uuidService.generate())
                //entity fields
                .entityId(user.getId())
                .entityUsername(user.getUsername())
                .entityPassword(user.getPassword())
                //event fields
                .eventUserId(userCreatedEvent.getId())
                .eventEmail(userCreatedEvent.getEmail())
                .eventPhone(userCreatedEvent.getPhone())
                .eventNickname(userCreatedEvent.getNickname())
                .eventName(userCreatedEvent.getName())
                .eventSurname(userCreatedEvent.getSurname())
                .build();

        //save transaction for outbox pattern
        createUserTransaction = saveTransaction(createUserTransaction);

        user = saveUserAndSendEvent(user, userCreatedEvent);

        log.info("User {} created", user.getUsername());

        //delete after commit transaction
        deleteCreateUserTransaction(createUserTransaction.getId());


        //generate jwt
        return new JwtDto(jwtService.generateRefresh(user), jwtService.generateAccess(user));
    }

    @Transactional
    private void deleteCreateUserTransaction (String id) {
        createUserTransactionRepository.deleteById(id);
    }

    @Transactional
    private CreateUserTransaction saveTransaction(CreateUserTransaction createUserTransaction) {
        return createUserTransactionRepository.save(createUserTransaction);
    }

    @Transactional
    private User saveUserAndSendEvent(User user, UserCreatedEvent userCreatedEvent) {
        user = userRepository.save(user);
        userCreatedEventsProducer.sendUserCreatedEvent(userCreatedEvent);
        return user;
    }
}
