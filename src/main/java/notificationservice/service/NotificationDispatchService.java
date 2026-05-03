package notificationservice.service;

import java.util.Locale;
import notificationservice.dto.NotificationOperation;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatchService {
    private final EmailNotificationService emailNotificationService;

    public NotificationDispatchService(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    public void dispatch(String email, NotificationOperation operation) {
        emailNotificationService.sendNotification(normalizeEmail(email), operation);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
