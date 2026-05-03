package notificationservice.service;

import notificationservice.dto.NotificationOperation;
import notificationservice.exception.NotificationDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {
    private final JavaMailSender javaMailSender;
    private final NotificationMessageFactory notificationMessageFactory;
    private final String from;

    public EmailNotificationService(JavaMailSender javaMailSender,
                                    NotificationMessageFactory notificationMessageFactory,
                                    @Value("${app.mail.from}") String from) {
        this.javaMailSender = javaMailSender;
        this.notificationMessageFactory = notificationMessageFactory;
        this.from = from;
    }

    public void sendNotification(String email, NotificationOperation operation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(notificationMessageFactory.buildSubject(operation));
        message.setText(notificationMessageFactory.buildBody(operation));

        try {
            javaMailSender.send(message);
        } catch (MailException ex) {
            throw new NotificationDeliveryException(
                    NotificationMessageConstants.EMAIL_DELIVERY_ERROR.formatted(email),
                    ex
            );
        }
    }
}
