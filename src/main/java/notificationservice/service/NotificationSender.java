package notificationservice.service;

/**
 * Контракт канала отправки уведомлений.
 */
public interface NotificationSender {

    /**
     * Проверяет, может ли канал обработать указанную команду.
     *
     * @param command команда отправки уведомления
     * @return {@code true}, если канал может выполнить отправку
     */
    boolean supports(NotificationCommand command);

    /**
     * Выполняет отправку уведомления через конкретный канал.
     *
     * @param command команда отправки уведомления
     */
    void send(NotificationCommand command);
}