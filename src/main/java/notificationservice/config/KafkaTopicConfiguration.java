package notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Конфигурация Kafka topic для уведомлений пользователей.
 */
@Configuration
public class KafkaTopicConfiguration {

    /**
     * Создаёт topic для обмена уведомлениями.
     *
     * @param topic имя topic
     * @param partitions количество partition
     * @param replicas количество реплик
     * @return описание topic для регистрации в Kafka
     */
    @Bean
    public NewTopic userNotificationsTopic(@Value("${app.kafka.notifications.topic}") String topic,
                                           @Value("${app.kafka.notifications.partitions:1}") int partitions,
                                           @Value("${app.kafka.notifications.replicas:1}") short replicas) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}