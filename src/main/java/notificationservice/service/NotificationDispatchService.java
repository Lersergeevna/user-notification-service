package notificationservice.service;

import notificationservice.dto.NotificationOperation;
import notificationservice.exception.NotificationDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Координирует отправку уведомлений независимо от источника события.
 */
@Service
public class NotificationDispatchService {
    private static final Logger log = LoggerFactory.getLogger(NotificationDispatchService.class);

    private final List<NotificationSender> notificationSenders;

    public NotificationDispatchService(List<NotificationSender> notificationSenders) {
        Assert.notEmpty(notificationSenders, "Должен быть настроен хотя бы один канал отправки уведомлений.");
        this.notificationSenders = List.copyOf(notificationSenders);
    }

    /**
     * Отправляет уведомление пользователю.
     *
     * @param email адрес электронной почты получателя
     * @param operation тип операции пользователя
     */
    public void dispatch(String email, NotificationOperation operation) {
        dispatch(new NotificationCommand(email, operation));
    }

    /**
     * Отправляет уведомление по нормализованной команде.
     *
     * @param command команда отправки уведомления
     */
    public void dispatch(NotificationCommand command) {
        boolean attempted = false;
        boolean delivered = false;
        NotificationDeliveryException lastDeliveryException = null;

        for (NotificationSender notificationSender : notificationSenders) {
            if (!notificationSender.supports(command)) {
                continue;
            }

            attempted = true;
            try {
                notificationSender.send(command);
                delivered = true;
            } catch (NotificationDeliveryException ex) {
                lastDeliveryException = ex;
                log.error("Канал {} не смог доставить уведомление для {}.",
                        notificationSender.getClass().getSimpleName(), command.email(), ex);
            }
        }

        if (!attempted) {
            log.warn("Не найден подходящий канал отправки для команды: {}", command);
            return;
        }

        if (!delivered && lastDeliveryException != null) {
            throw lastDeliveryException;
        }
    }
}