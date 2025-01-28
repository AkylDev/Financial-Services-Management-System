package kz.projects.notificationservice.service;

import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class NotificationEventConsumer {
  private final MailSenderService mailSenderService;

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
    var htmlLetterString = createLetter(notificationEvent.getUsername(), notificationEvent.getMessage());

    mailSenderService.sendMail(
            notificationEvent.getEmail(),
            "Advanced Financial Services Management System. Операции со счетом!",
            htmlLetterString);
    log.info("Notification event send to email " + notificationEvent.getEmail());
  }

  private String createLetter(String name, String message) {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("letters/email-message.html")) {
      if (inputStream == null) {
        throw new IllegalArgumentException("HTML file not found: letters/email-message.html");
      }
      String htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

      htmlTemplate = htmlTemplate.replace("{{name}}", name);
      htmlTemplate = htmlTemplate.replace("{{message}}", message);

      return htmlTemplate;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read HTML file", e);
    }
  }
}
