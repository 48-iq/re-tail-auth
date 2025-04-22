package dev.ilya_anna.auth_service.producers;

import dev.ilya_anna.auth_service.events.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class UserCreatedEventsProducer {

    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;


    public void sendUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        try {
            SendResult<String, Map<String, Object>> result = kafkaTemplate.send("user-created-events-topic",
                    userCreatedEvent.getId(),
                    UserCreatedEvent.toMap(userCreatedEvent)).get();
            log.info("Message sent successfully: {}", result.getRecordMetadata());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while sending message", e);

        } catch (ExecutionException e) {
            log.error("Failed to send message", e.getCause());
        }
    }
}
