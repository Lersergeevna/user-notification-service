package notificationservice.exception;

/**
 * Исключение, возникающее при недоступности канала доставки уведомления.
 */
public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}