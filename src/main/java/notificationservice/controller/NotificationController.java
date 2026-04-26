package notificationservice.controller;

import jakarta.validation.Valid;
import notificationservice.dto.SendNotificationRequest;
import notificationservice.service.NotificationCommand;
import notificationservice.service.NotificationDispatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для ручной отправки уведомлений по электронной почте.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationDispatchService notificationDispatchService;

    public NotificationController(NotificationDispatchService notificationDispatchService) {
        this.notificationDispatchService = notificationDispatchService;
    }

    /**
     * Отправляет email-уведомление по входным данным запроса.
     *
     * @param request данные для отправки уведомления
     */
    @PostMapping("/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmailNotification(@Valid @RequestBody SendNotificationRequest request) {
        notificationDispatchService.dispatch(new NotificationCommand(request.email(), request.operation()));
    }
}