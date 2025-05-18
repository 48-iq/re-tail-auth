package dev.ilya_anna.auth_service.controllers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

import dev.ilya_anna.auth_service.dto.JwtDto;
import dev.ilya_anna.auth_service.entities.Role;
import dev.ilya_anna.auth_service.entities.User;
import dev.ilya_anna.auth_service.repositories.CreateUserTransactionRepository;
import dev.ilya_anna.auth_service.repositories.SignOutMarkRepository;
import dev.ilya_anna.auth_service.repositories.UserRepository;
import dev.ilya_anna.auth_service.services.JwtService;
import dev.ilya_anna.auth_service.services.UuidService;

@Testcontainers
@DirtiesContext
@Slf4j
@SpringBootTest(
    properties = {
        "app.jwt.issuer=auth_service",
        "app.jwt.subject=user_details",
        "app.jwt.access.duration=1000",
        "app.jwt.refresh.duration=2000",
        "app.jwt.access.secret=access_secret",
        "app.jwt.refresh.secret=refresh_secret",
        "eureka.client.enabled=false",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "app.uuid.seed=auth_service"
    },
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EmbeddedKafka(topics = {"user-created-events-topic", "user-sign-out-events-topic"})
public class AuthControllerTests {
    
    @Container
    @ServiceConnection
    static KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    static {
        postgres.withDatabaseName("auth_service");
        postgres.withUsername("postgres");
        postgres.withPassword("postgres");
        postgres.withInitScript("init.sql");
    }

    @BeforeAll
    static void beforeAll() {
        redis.start();
        redis.withEnv("REDIS_PASSWORD", "password");
        postgres.start();
        kafka.start();
    }
    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:latest"));


    @AfterAll
    static void afterAll() {    
        redis.stop();
        postgres.stop();
        kafka.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.database", () -> "0");
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.data.redis.password", () -> "password");
    }

    @Getter
    @Setter
    static class TestConsumer {
        private List<Map<String, Object>> userCreatedEventMessages = new ArrayList<>();
        private List<Map<String, Object>> userSignOutEventMessages = new ArrayList<>();

        @KafkaListener(topics = "user-created-events-topic")
        public void onUserCreatedEvent(Map<String, Object> message) {
            userCreatedEventMessages.add(message);
        }

        @KafkaListener(topics = "user-sign-out-events-topic")
        public void onUserSignOutEvent(Map<String, Object> message) {
            userSignOutEventMessages.add(message);
        }

        public void clear() {
            userCreatedEventMessages.clear();
            userSignOutEventMessages.clear();
        }
    }

    @TestConfiguration
    static class Config {
        @Bean
        public TestConsumer testConsumer() {
            return new TestConsumer();
        }
    }

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateUserTransactionRepository cutRepository;

    @Autowired
    private SignOutMarkRepository somRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UuidService uuidService;

    @LocalServerPort
    private Integer port;

    @Autowired
    private TestConsumer testConsumer;

    @BeforeEach
    void beforeEach() {
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1/auth";
        userRepository.deleteAll();
        cutRepository.deleteAll();
        somRepository.deleteAll();
        testConsumer.clear();
    }
    
    @Test
    void signIn_ReturnsTokens_WhenCredentialsAreValid() {
        //create user in db
        String username = "test_user";
        String password = "test_password";
        User user = User.builder()
            .id(uuidService.generate())
            .username(username)
            .password(passwordEncoder.encode(password))
            .roles(List.of(new Role("ROLE_USER")))
            .build();
        userRepository.save(user);

        //check returned tokens after sign in
        JwtDto result = given()
            .contentType(ContentType.JSON)
            .when()
            .body(Map.of(
                "username", username,
                "password", password
            ))
            .post("/sign-in")
            .then()
            .log().body()
            .statusCode(200).extract().as(JwtDto.class);
        assertDoesNotThrow(() -> jwtService.verifyAccessToken(result.getAccess()));
        assertDoesNotThrow(() -> jwtService.verifyRefreshToken(result.getRefresh()));
    }

    @Test
    void signIn_ReturnsForbidden_WhenCredentialsAreInvalid() {
        String username = "test_user";
        String password = "test_password";
        User user = User.builder()
            .id(uuidService.generate())
            .username(username)
            .password(passwordEncoder.encode(password))
            .roles(List.of(new Role("ROLE_USER")))
            .build();
        userRepository.save(user);

        given()
            .contentType(ContentType.JSON)
            .when()
            .body(Map.of(
                "username", username,
                "password", "incorrect_password"
            ))
            .post("/sign-in")
            .then()
            .log().body()
            .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    void signUp_ReturnsTokens_WhenRegisterNewUser() {
        JwtDto result = given()
            .contentType(ContentType.JSON)
            .when()
            .body(Map.of(
                "username", "new_user",
                "password", "new_UserPASSWORD#!",
                "email", "test@mail.ru",
                "phone", "+79999999999",
                "nickname", "new_user_nickname",
                "name", "Jhon",
                "surname", "Doe",
                "roles", List.of("ROLE_USER")
            ))
            .post("/sign-up")
            .then()
            .log().body()
            .statusCode(200).extract().as(JwtDto.class);
        assertDoesNotThrow(() -> jwtService.verifyAccessToken(result.getAccess()));
        assertDoesNotThrow(() -> jwtService.verifyRefreshToken(result.getRefresh()));
    }

    @Test
    void signUp_SaveUserToDB_WhenRegisterNewUser() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .body(Map.of(
                "username", "new_user",
                "password", "new_UserPASSWORD#!",
                "email", "test@mail.ru",
                "phone", "+79999999999",
                "nickname", "new_user_nickname",
                "name", "Jhon",
                "surname", "Doe",
                "roles", List.of("ROLE_USER")
            ))
            .post("/sign-up")
            .then()
            .log().body()
            .statusCode(200);
        User user = userRepository.findByUsername("new_user").get();
        assertNotNull(user);
    }

    @Test
    void signUp_SendMessageToKafka_WhenRegisterNewUser() {
        given()
            .contentType(ContentType.JSON)
            .when()
            .body(Map.of(
                "username", "new_user",
                "password", "new_UserPASSWORD#!",
                "email", "test@mail.ru",
                "phone", "+79999999999",
                "nickname", "new_user_nickname",
                "name", "Jhon",
                "surname", "Doe",
                "roles", List.of("ROLE_USER")
            ))
            .post("/sign-up")
            .then()
            .log().body()
            .statusCode(200);

        await()
            .pollInterval(Duration.ofSeconds(3))
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                if (!testConsumer.getUserCreatedEventMessages().isEmpty()) 
                    log.info("message: {}", testConsumer.getUserCreatedEventMessages().getLast());
                assertNotNull(testConsumer.getUserCreatedEventMessages().getLast());
                testConsumer.clear();
            });
    }


    
}
