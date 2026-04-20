package notificationservice.service.provider;

import notificationservice.dto.NotificationOperation;
import notificationservice.service.NotificationMessage;

/**
 * Контракт провайдера текста уведомления для конкретного типа операции.
 */
public interface NotificationMessageProvider {

    /**
     * Возвращает тип операции, который обслуживает провайдер.
     *
     * @return тип операции пользователя
     */
    NotificationOperation supportedOperation();

    /**
     * Формирует готовое сообщение для отправки.
     *
     * @return тема и текст письма
     */
    NotificationMessage buildMessage();
}