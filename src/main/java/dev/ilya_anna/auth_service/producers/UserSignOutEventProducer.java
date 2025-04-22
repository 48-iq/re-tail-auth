package dev.ilya_anna.auth_service.producers;

import dev.ilya_anna.auth_service.events.UserSignOutEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class UserSignOutEventProducer {
    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void sendUserSignOutEvent(UserSignOutEvent userSignOutEvent) {
        UserSignOutEvent.toMap(userSignOutEvent);
        try {
            SendResult<String, Map<String, Object>> result = kafkaTemplate.send("user-sign-out-events-topic",
                    userSignOutEvent.getId(),
                    UserSignOutEvent.toMap(userSignOutEvent)).get();
            log.info("Message sent successfully: {}", result.getRecordMetadata());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending message", e);

        } catch (ExecutionException e) {
            log.error("Failed to send message", e.getCause());
        }
    }
}
