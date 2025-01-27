package kz.projects.ams.services.impl;

import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationEventProducerImpl implements NotificationEventProducer {
  private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

  @Override
  public void publishEvent(NotificationEvent event, String topicName) {
    CompletableFuture<SendResult<String, NotificationEvent>> future = kafkaTemplate.send(topicName, event);
    future.whenComplete((result, ex) -> {
      if (ex == null) {
        System.out.println("Sent message=[" + event +
                "] with offset=[" + result.getRecordMetadata().offset() + "]");
      } else {
        System.out.println("Unable to send message=[" +
                event + "] due to : " + ex.getMessage());
      }
    });
  }
}
