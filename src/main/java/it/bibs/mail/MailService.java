package it.bibs.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;
  private final MailProperties mailProperties;

  @Async
  public void sendMail(final String mailTo, final String subject, final String html) {
    log.info("sending mail {} to {}", subject, mailTo);

    javaMailSender.send(
        mimeMessage -> {
          final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
          message.setFrom(mailProperties.getMailFrom(), mailProperties.getMailDisplayName());
          message.setTo(mailTo);
          message.setSubject(subject);
          message.setText(html, true);
        });

    log.info("sending completed");
  }
}
