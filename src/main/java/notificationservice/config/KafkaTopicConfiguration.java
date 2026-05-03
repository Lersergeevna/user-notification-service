package notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic userNotificationsTopic(@Value("${app.kafka.notifications.topic}") String topic,
                                           @Value("${app.kafka.notifications.partitions}") int partitions,
                                           @Value("${app.kafka.notifications.replicas}") short replicas) {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
