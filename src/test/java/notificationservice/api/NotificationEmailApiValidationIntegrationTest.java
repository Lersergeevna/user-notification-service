package notificationservice.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import notificationservice.support.AbstractGreenMailIntegrationTest;

/**
 * Интеграционные тесты валидации REST API отправки уведомлений.
 */
@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationEmailApiValidationIntegrationTest extends AbstractGreenMailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendEmail_shouldReturnBadRequestForInvalidOperation() throws Exception {
        String requestBody = """
                {
                  "email": "lera@example.com",
                  "operation": "CREATE"
                }
                """;

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendEmail_shouldReturnBadRequestForInvalidEmail() throws Exception {
        String requestBody = """
                {
                  "email": "not-an-email",
                  "operation": "CREATED"
                }
                """;

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        assertThat(GREEN_MAIL.getReceivedMessages()).isEmpty();
    }
}