package notificationservice.kafka;

import notificationservice.dto.UserNotificationEvent;
import notificationservice.service.NotificationDispatchService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaListener {
    private final NotificationDispatchService notificationDispatchService;

    public NotificationKafkaListener(NotificationDispatchService notificationDispatchService) {
        this.notificationDispatchService = notificationDispatchService;
    }

    @KafkaListener(topics = "${app.kafka.notifications.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserNotificationEvent event) {
        notificationDispatchService.dispatch(event.email(), event.operation());
    }
}
