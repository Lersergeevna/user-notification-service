package notificationservice.service;

import notificationservice.exception.NotificationDeliveryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Канал отправки email-уведомлений пользователю.
 */
@Service
public class EmailNotificationService implements NotificationSender {
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

    /**
     * Проверяет, можно ли отправить уведомление по электронной почте.
     *
     * @param command команда отправки уведомления
     * @return {@code true}, если в команде есть email и операция
     */
    @Override
    public boolean supports(NotificationCommand command) {
        return command.email() != null
                && !command.email().isBlank()
                && command.operation() != null;
    }

    /**
     * Формирует и отправляет email-уведомление.
     *
     * @param command команда отправки уведомления
     */
    @Override
    public void send(NotificationCommand command) {
        NotificationMessage notificationMessage = notificationMessageFactory.build(command.operation());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(command.email());
        mailMessage.setSubject(notificationMessage.subject());
        mailMessage.setText(notificationMessage.body());

        try {
            javaMailSender.send(mailMessage);
        } catch (MailException ex) {
            throw new NotificationDeliveryException(
                    "Не удалось отправить email-уведомление пользователю %s.".formatted(command.email()),
                    ex
            );
        }
    }
}