package notificationservice.service.provider;

import notificationservice.dto.NotificationOperation;
import notificationservice.service.NotificationMessage;
import org.springframework.stereotype.Component;

/**
 * Провайдер текста уведомления об удалении аккаунта.
 */
@Component
public class UserDeletedNotificationMessageProvider implements NotificationMessageProvider {

    @Override
    public NotificationOperation supportedOperation() {
        return NotificationOperation.DELETED;
    }

    @Override
    public NotificationMessage buildMessage() {
        return new NotificationMessage(
                "Уведомление об удалении аккаунта",
                "Здравствуйте! Ваш аккаунт был удалён."
        );
    }
}