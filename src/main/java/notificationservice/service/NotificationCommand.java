package notificationservice.service;

import notificationservice.dto.NotificationOperation;

import java.util.Locale;

/**
 * Команда на отправку уведомления, независимая от источника запроса.
 *
 * @param email адрес электронной почты получателя
 * @param operation тип операции пользователя
 */
public record NotificationCommand(
        String email,
        NotificationOperation operation
) {

    /**
     * Нормализует email при создании команды, чтобы все каналы получали единые данные.
     */
    public NotificationCommand {
        email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}