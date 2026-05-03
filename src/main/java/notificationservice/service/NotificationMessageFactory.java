package notificationservice.service;

import notificationservice.dto.NotificationOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFactory {
    private final String siteName;

    public NotificationMessageFactory(@Value("${app.site.name}") String siteName) {
        this.siteName = siteName;
    }

    public String buildSubject(NotificationOperation operation) {
        return switch (operation) {
            case CREATED -> NotificationMessageConstants.CREATED_SUBJECT;
            case DELETED -> NotificationMessageConstants.DELETED_SUBJECT;
        };
    }

    public String buildBody(NotificationOperation operation) {
        return switch (operation) {
            case CREATED -> NotificationMessageConstants.CREATED_BODY_TEMPLATE.formatted(siteName);
            case DELETED -> NotificationMessageConstants.DELETED_BODY;
        };
    }
}
