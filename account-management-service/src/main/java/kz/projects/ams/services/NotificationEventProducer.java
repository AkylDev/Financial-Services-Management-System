package kz.projects.ams.services;

import kz.projects.commonlib.dto.NotificationEvent;

public interface NotificationEventProducer {
  void publishEvent(NotificationEvent event, String topicName);
}
