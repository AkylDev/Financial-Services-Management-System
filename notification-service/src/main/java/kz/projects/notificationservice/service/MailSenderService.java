package kz.projects.notificationservice.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String from;

  public void sendMail(String to, String subject, String text) {
    try {
      var message = mailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text, true);
      helper.setFrom(from);

      mailSender.send(message);
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
