package notificationservice.service.provider;

import notificationservice.dto.NotificationOperation;
import notificationservice.service.NotificationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Провайдер текста уведомления о создании аккаунта.
 */
@Component
public class UserCreatedNotificationMessageProvider implements NotificationMessageProvider {
    private final String siteName;

    public UserCreatedNotificationMessageProvider(@Value("${app.site.name}") String siteName) {
        this.siteName = siteName;
    }

    @Override
    public NotificationOperation supportedOperation() {
        return NotificationOperation.CREATED;
    }

    @Override
    public NotificationMessage buildMessage() {
        return new NotificationMessage(
                "Уведомление о создании аккаунта",
                "Здравствуйте! Ваш аккаунт на сайте \"%s\" был успешно создан.".formatted(siteName)
        );
    }
}