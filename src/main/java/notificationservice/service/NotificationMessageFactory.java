package notificationservice.service;

import notificationservice.dto.NotificationOperation;
import notificationservice.service.provider.NotificationMessageProvider;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Фабрика готовых email-сообщений по типу операции пользователя.
 */
@Component
public class NotificationMessageFactory {
    private final Map<NotificationOperation, NotificationMessageProvider> providers;

    public NotificationMessageFactory(List<NotificationMessageProvider> providers) {
        this.providers = createProviderMap(providers);
    }

    /**
     * Возвращает тему и текст письма для указанной операции.
     *
     * @param operation тип операции пользователя
     * @return готовое сообщение для отправки
     */
    public NotificationMessage build(NotificationOperation operation) {
        NotificationMessageProvider provider = providers.get(operation);
        if (provider == null) {
            throw new IllegalArgumentException("Для операции %s не найден провайдер уведомления.".formatted(operation));
        }
        return provider.buildMessage();
    }

    private Map<NotificationOperation, NotificationMessageProvider> createProviderMap(
            List<NotificationMessageProvider> providers
    ) {
        Map<NotificationOperation, NotificationMessageProvider> providerMap = new EnumMap<>(NotificationOperation.class);
        for (NotificationMessageProvider provider : providers) {
            NotificationMessageProvider previous = providerMap.put(provider.supportedOperation(), provider);
            if (previous != null) {
                throw new IllegalStateException(
                        "Для операции %s зарегистрировано несколько провайдеров уведомлений."
                                .formatted(provider.supportedOperation())
                );
            }
        }
        return Map.copyOf(providerMap);
    }
}