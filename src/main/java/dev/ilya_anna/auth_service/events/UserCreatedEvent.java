package dev.ilya_anna.auth_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent {
    private String id;
    private String userId;
    private String name;
    private String surname;
    private String nickname;
    private String phone;
    private String email;

    public static final String TOPIC = "user-created-events-topic";

    public static Map<String, Object> toMap(UserCreatedEvent userCreatedEvent) {
        return Map.of(
                "id", userCreatedEvent.getId(),
                "userId", userCreatedEvent.getUserId(),
                "name", userCreatedEvent.getName(),
                "surname", userCreatedEvent.getSurname(),
                "nickname", userCreatedEvent.getNickname(),
                "phone", userCreatedEvent.getPhone(),
                "email", userCreatedEvent.getEmail()
        );
    }

    public static UserCreatedEvent fromMap(Map<String, Object> map) {
        return UserCreatedEvent.builder()
                .id((String) map.get("id"))
                .userId((String) map.get("userId"))
                .name((String) map.get("name"))
                .surname((String) map.get("surname"))
                .nickname((String) map.get("nickname"))
                .phone((String) map.get("phone"))
                .email((String) map.get("email"))
                .build();
    }
}
