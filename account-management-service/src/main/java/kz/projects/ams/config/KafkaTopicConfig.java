package kz.projects.ams.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapAddress;

  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    return new KafkaAdmin(configs);
  }

  @Bean
  public NewTopic accountTopic() {
    return new NewTopic("topic-account", 1, (short) 1);
  }

  @Bean
  public NewTopic investmentTopic() {
    return new NewTopic("topic-investment", 1, (short) 1);
  }

  @Bean
  public NewTopic transactionsTopic() {
    return new NewTopic("topic-transactions", 1, (short) 1);
  }

  @Bean
  public NewTopic advisoryTopic() {
    return new NewTopic("topic-advisory", 1, (short) 1);
  }
}
