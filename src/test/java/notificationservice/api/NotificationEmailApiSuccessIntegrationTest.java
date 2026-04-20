package notificationservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import notificationservice.support.AbstractGreenMailIntegrationTest;

/**
 * Интеграционные тесты успешной отправки письма через REST API.
 */
@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationEmailApiSuccessIntegrationTest extends AbstractGreenMailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendEmail_shouldSendCreatedNotification() throws Exception {
        String requestBody = """
                {
                  "email": "Lera@Example.com",
                  "operation": "CREATED"
                }
                """;

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isAccepted());

        MimeMessage[] receivedMessages = waitForEmails(1, 5000);
        assertThat(receivedMessages).hasSize(1);
        assertThat(receivedMessages[0].getAllRecipients()[0].toString()).isEqualTo("lera@example.com");
        assertThat(receivedMessages[0].getSubject()).isEqualTo("Уведомление о создании аккаунта");
        assertThat(receivedMessages[0].getContent().toString())
                .contains("Здравствуйте! Ваш аккаунт на сайте \"Тестовый сайт\" был успешно создан.");
    }
}