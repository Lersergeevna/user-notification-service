package notificationservice.controller;

import jakarta.validation.Valid;
import notificationservice.dto.SendNotificationRequest;
import notificationservice.service.NotificationDispatchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationDispatchService notificationDispatchService;

    public NotificationController(NotificationDispatchService notificationDispatchService) {
        this.notificationDispatchService = notificationDispatchService;
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendEmailNotification(@Valid @RequestBody SendNotificationRequest request) {
        notificationDispatchService.dispatch(request.email(), request.operation());
    }
}
