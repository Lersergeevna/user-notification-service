package notificationservice.dto;

/**
 * Событие уведомления о пользователе, получаемое из Kafka.
 *
 * @param eventId уникальный идентификатор события для дедупликации
 * @param email адрес электронной почты пользователя
 * @param operation тип операции над пользователем
 */
public record UserNotificationEvent(
        String eventId,
        String email,
        NotificationOperation operation
) {
}