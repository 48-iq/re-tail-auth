package dev.ilya_anna.auth_service.entities;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@RedisHash
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignOutMark {
    @Id
    private String id;
    private String userId;
    private LocalDateTime signOutTime;
    @TimeToLive
    private Long ttl;
}
