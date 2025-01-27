package kz.projects.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationEvent {
  private String userId;
  private String username;
  private String email;
  private String message;
  private String timestamp;

  @Override
  public String toString() {
    return "NotificationEvent{" +
            "userId='" + userId + '\'' +
            ", message='" + message + '\'' +
            ", timestamp='" + timestamp + '\'' +
            '}';
  }
}
