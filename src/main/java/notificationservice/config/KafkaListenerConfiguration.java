package notificationservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Конфигурация обработки ошибок Kafka listener.
 */
@Configuration
public class KafkaListenerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(KafkaListenerConfiguration.class);

    /**
     * Настраивает ограниченное количество повторных попыток при сбоях обработки Kafka-сообщения.
     *
     * @return обработчик ошибок для Kafka listener
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(new FixedBackOff(1_000L, 2L));
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn(
                        "Повторная обработка Kafka-сообщения key={} attempt={}",
                        record == null ? null : record.key(),
                        deliveryAttempt,
                        ex
                )
        );
        return errorHandler;
    }
}