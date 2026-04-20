package notificationservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import notificationservice.dto.UserNotificationEvent;
import notificationservice.exception.NotificationDeliveryException;
import notificationservice.service.NotificationCommand;
import notificationservice.service.NotificationDispatchService;
import notificationservice.service.ProcessedKafkaEventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener, получающий события о пользователях и запускающий отправку уведомления.
 */
@Component
public class NotificationKafkaListener {
    private static final Logger log = LoggerFactory.getLogger(NotificationKafkaListener.class);

    private final NotificationDispatchService notificationDispatchService;
    private final ProcessedKafkaEventStore processedKafkaEventStore;
    private final ObjectMapper objectMapper;

    public NotificationKafkaListener(NotificationDispatchService notificationDispatchService,
                                     ProcessedKafkaEventStore processedKafkaEventStore,
                                     ObjectMapper objectMapper) {
        this.notificationDispatchService = notificationDispatchService;
        this.processedKafkaEventStore = processedKafkaEventStore;
        this.objectMapper = objectMapper;
    }

    /**
     * Обрабатывает JSON-сообщение из Kafka и запускает отправку уведомления.
     *
     * @param payload JSON-представление события уведомления
     */
    @KafkaListener(topics = "${app.kafka.notifications.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String payload) {
        try {
            UserNotificationEvent event = objectMapper.readValue(payload, UserNotificationEvent.class);

            if (event.eventId() == null || event.eventId().isBlank()
                    || event.email() == null || event.email().isBlank()
                    || event.operation() == null) {
                log.warn("Пропущено некорректное Kafka-сообщение: {}", payload);
                return;
            }

            if (!processedKafkaEventStore.tryStartProcessing(event.eventId())) {
                log.info("Повторное Kafka-событие eventId={} пропущено.", event.eventId());
                return;
            }

            try {
                notificationDispatchService.dispatch(new NotificationCommand(event.email(), event.operation()));
                processedKafkaEventStore.markProcessed(event.eventId());
            } catch (NotificationDeliveryException ex) {
                processedKafkaEventStore.markFailed(event.eventId());
                log.error("Не удалось доставить уведомление по Kafka-событию eventId={}", event.eventId(), ex);
                throw ex;
            }
        } catch (JsonProcessingException ex) {
            log.error("Не удалось прочитать сообщение из Kafka: {}", payload, ex);
        }
    }
}