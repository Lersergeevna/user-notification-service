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
 * Интеграционные тесты доставки уведомлений через Kafka.
 */
@SpringBootTest(properties = "spring.kafka.consumer.group-id=kafka-delivery-test-group")
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "user-notifications")
class KafkaNotificationDeliveryIntegrationTest extends AbstractGreenMailIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldSendEmailAfterKafkaMessage() throws Exception {
        String event = """
                {
                  "eventId": "%s",
                  "email": "Lera@Example.com",
                  "operation": "DELETED"
                }
                """.formatted(UUID.randomUUID());

        kafkaTemplate.send("user-notifications", event).get();

        MimeMessage[] receivedMessages = waitForEmails(1, 7000);
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getAllRecipients()[0].toString()).isEqualTo("lera@example.com");
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Уведомление об удалении аккаунта");
        assertThat(receivedMessages[0].getContent().toString())
                .contains("Здравствуйте! Ваш аккаунт был удалён.");
    }
}