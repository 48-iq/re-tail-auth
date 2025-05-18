package dev.ilya_anna.auth_service.entities;

import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "create_user_transactions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CreateUserTransaction {

    @Id
    private String id;

    private String entityId;
    private String entityUsername;
    private String entityPassword;

    private String eventId;
    private String eventUserId;
    private String eventName;
    private String eventSurname;
    private String eventNickname;
    private String eventPhone;
    private String eventEmail;
    private ZonedDateTime eventTime;

}
