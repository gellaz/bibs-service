package it.bibs.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class MailProperties {

  @Value("${app.mail.from}")
  private String mailFrom;

  @Value("${app.mail.displayName}")
  private String mailDisplayName;
}
