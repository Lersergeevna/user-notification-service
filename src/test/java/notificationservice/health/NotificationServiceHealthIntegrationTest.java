package notificationservice.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "management.health.defaults.enabled=false",
        "management.health.ping.enabled=true"
})
class NotificationServiceHealthIntegrationTest {

    @Test
    void context_shouldStartSuccessfully() {
    }
}