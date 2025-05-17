package dev.ilya_anna.auth_service.controllers;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@Testcontainers
@SpringBootTest(
    properties = {
        "app.jwt.issuer=auth_service",
        "app.jwt.subject=user_details",
        "app.jwt.access.duration=1000",
        "app.jwt.refresh.duration=2000",
        "app.jwt.access.secret=access_secret",
        "app.jwt.refresh.secret=refresh_secret",
        "eureka.client.enabled=false",
        "app.uuid.seed=auth_service"
    }
)
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
        registry.add("spring.data.redis.port", () -> String.valueOf(redis.getFirstMappedPort()));
        registry.add("spring.data.redis.password", () -> "password");
    }

    @Test
    void contextLoads() {
    }
}
