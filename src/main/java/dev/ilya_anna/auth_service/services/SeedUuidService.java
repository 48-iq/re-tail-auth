package dev.ilya_anna.auth_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class SeedUuidService implements UuidService {
    @Value("${app.uuid.seed}")
    private String seed;

    /**
     * Generates a UUID that is unique among all services in the infrastructure.
     * This is done by adding a seed before the generated UUID.
     *
     * @return a unique UUID
     */
    @Override
    public String generate() {
        //add a seed before the uuid to ensure uniqueness with other services
        return seed + UUID.randomUUID();
    }
}
