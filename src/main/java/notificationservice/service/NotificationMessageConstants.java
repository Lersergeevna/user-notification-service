package notificationservice.service;

public final class NotificationMessageConstants {
    public static final String CREATED_SUBJECT = "Уведомление о создании аккаунта";
    public static final String DELETED_SUBJECT = "Уведомление об удалении аккаунта";

    public static final String CREATED_BODY_TEMPLATE = "Здравствуйте! Ваш аккаунт на сайте \"%s\" был успешно создан.";
    public static final String DELETED_BODY = "Здравствуйте! Ваш аккаунт был удалён.";

    public static final String EMAIL_DELIVERY_ERROR = "Не удалось отправить email-уведомление пользователю %s.";
    public static final String VALIDATION_ERROR = "Ошибка валидации входных данных.";
    public static final String INVALID_REQUEST_BODY = "Некорректное тело запроса.";
    public static final String UNEXPECTED_ERROR = "Непредвиденная ошибка при обработке запроса.";

    private NotificationMessageConstants() {
    }
}
