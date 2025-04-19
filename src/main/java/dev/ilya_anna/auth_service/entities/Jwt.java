package dev.ilya_anna.auth_service.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;

@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Jwt {
    private String jwt;
    @TimeToLive
    private Long ttl;
}
