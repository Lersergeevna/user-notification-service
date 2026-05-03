package notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendNotificationRequest(
        @NotBlank(message = "Email must not be blank.")
        @Email(message = "Email has invalid format.")
        String email,

        @NotNull(message = "Operation must not be null.")
        NotificationOperation operation
) {
}
