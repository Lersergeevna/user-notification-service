package notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO запроса на отправку email-уведомления.
 *
 * @param email адрес электронной почты получателя
 * @param operation тип операции пользователя
 */
public record SendNotificationRequest(
        @NotBlank(message = "Электронная почта не должна быть пустой.")
        @Email(message = "Некорректный формат электронной почты.")
        String email,

        @NotNull(message = "Операция не должна быть пустой.")
        NotificationOperation operation
) {
}