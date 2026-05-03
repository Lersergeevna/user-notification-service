package notificationservice.dto;

public record UserNotificationEvent(
        String email,
        NotificationOperation operation
) {
}
