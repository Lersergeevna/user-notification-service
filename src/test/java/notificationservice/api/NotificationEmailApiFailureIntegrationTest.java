package notificationservice.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Интеграционный тест отказа отправки письма при недоступном SMTP.
 */
@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.mail.host=127.0.0.1",
        "spring.mail.port=25252",
        "spring.mail.properties.mail.smtp.connectiontimeout=500",
        "spring.mail.properties.mail.smtp.timeout=500",
        "spring.mail.properties.mail.smtp.writetimeout=500"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationEmailApiFailureIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendEmailNotification_shouldReturnServiceUnavailableWhenMailServerIsDown() throws Exception {
        String requestBody = """
                {
                  "email": "lera@example.com",
                  "operation": "CREATED"
                }
                """;

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isServiceUnavailable());
    }
}