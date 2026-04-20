package notificationservice.service;

/**
 * Модель готового сообщения для отправки пользователю.
 *
 * @param subject тема письма
 * @param body текст письма
 */
public record NotificationMessage(
        String subject,
        String body
) {
}