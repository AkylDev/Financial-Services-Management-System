package kz.projects.notificationservice.service;

import kz.projects.commonlib.dto.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class NotificationEventConsumer {
  Logger log = Logger.getLogger(this.getClass().getName());

  @KafkaListener(topics = "topic-account", groupId = "account-consumer-group",
          containerFactory = "kafkaListenerContainerFactory")
  public void consumeAccountEvent(NotificationEvent notificationEvent) {
    System.out.println("Event received " + notificationEvent);
    sendNotification(notificationEvent);
  }

  @KafkaListener(topics = "topic-transactions", groupId = "transactions-consumer-group",
          containerFactory = "kafkaListenerContainerFactory")
  public void consumeTransactionsEvent(NotificationEvent notificationEvent) {
    System.out.println("Event received " + notificationEvent);
    sendNotification(notificationEvent);
  }

  @KafkaListener(topics = "topic-investment", groupId = "investment-consumer-group",
          containerFactory = "kafkaListenerContainerFactory")
  public void consumeInvestmentEvent(NotificationEvent notificationEvent) {
    System.out.println("Event received " + notificationEvent);
    sendNotification(notificationEvent);
  }

  @KafkaListener(topics = "topic-advisory", groupId = "advisory-consumer-group",
          containerFactory = "kafkaListenerContainerFactory")
  public void consumeAdvisoryEvent(NotificationEvent notificationEvent) {
    System.out.println("Event received " + notificationEvent);
    sendNotification(notificationEvent);
  }

  private void sendNotification(NotificationEvent notificationEvent) {
    log.info("Notification event send to email " + notificationEvent.getEmail());
  }
}
