package notificationservice.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import notificationservice.support.AbstractGreenMailIntegrationTest;

/**
 * Интеграционный тест идемпотентности обработки Kafka-сообщений.
 */
@SpringBootTest(properties = "spring.kafka.consumer.group-id=kafka-idempotency-test-group")
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user-notifications")
class KafkaNotificationIdempotencyIntegrationTest extends AbstractGreenMailIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldNotSendDuplicateEmailForSameEventId() throws Exception {
        String eventId = UUID.randomUUID().toString();

        String event = """
                {
                  "eventId": "%s",
                  "email": "lera@example.com",
                  "operation": "CREATED"
                }
                """.formatted(eventId);

        kafkaTemplate.send("user-notifications", event).get();
        kafkaTemplate.send("user-notifications", event).get();

        MimeMessage[] receivedMessages = waitForEmails(1, 7000);
        assertThat(receivedMessages).hasSize(1);
    }
}