package org.binary.scripting.chgamesservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Value("${app.kafka.topic.score-submitted}")
    private String scoreSubmittedTopic;

    @Bean
    public NewTopic scoreSubmittedTopic() {
        return TopicBuilder.name(scoreSubmittedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
