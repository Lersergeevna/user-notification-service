package notificationservice.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Хранилище идентификаторов Kafka-событий, уже находящихся в обработке
 * или успешно обработанных.
 * Используется для защиты от повторной отправки одного и того же уведомления.
 */
@Service
public class ProcessedKafkaEventStore {

    private final ConcurrentHashMap<String, EventEntry> processedEvents = new ConcurrentHashMap<>();
    private final int maxSize;
    private final Duration retention;

    public ProcessedKafkaEventStore(
            @Value("${app.notifications.idempotency.max-size:1000}") int maxSize,
            @Value("${app.notifications.idempotency.retention:PT24H}") Duration retention
    ) {
        this.maxSize = maxSize;
        this.retention = retention;
    }

    /**
     * Пытается начать обработку события.
     *
     * @param eventId идентификатор события
     * @return {@code true}, если событие ещё не обрабатывалось и обработка разрешена;
     *         {@code false}, если событие уже находится в работе или было обработано ранее
     */
    public synchronized boolean tryStartProcessing(String eventId) {
        cleanupExpired();

        if (processedEvents.containsKey(eventId)) {
            return false;
        }

        ensureCapacity();
        processedEvents.put(eventId, new EventEntry(EventStatus.PROCESSING, Instant.now()));
        return true;
    }

    /**
     * Проверяет, было ли событие уже успешно обработано.
     *
     * @param eventId идентификатор события
     * @return {@code true}, если событие уже обработано
     */
    public synchronized boolean isProcessed(String eventId) {
        cleanupExpired();
        EventEntry entry = processedEvents.get(eventId);
        return entry != null && entry.status() == EventStatus.PROCESSED;
    }

    /**
     * Помечает событие как успешно обработанное.
     *
     * @param eventId идентификатор события
     */
    public synchronized void markProcessed(String eventId) {
        cleanupExpired();
        processedEvents.put(eventId, new EventEntry(EventStatus.PROCESSED, Instant.now()));
    }

    /**
     * Снимает отметку о текущей обработке события после неуспешной доставки,
     * чтобы Kafka могла повторить попытку.
     *
     * @param eventId идентификатор события
     */
    public synchronized void markFailed(String eventId) {
        processedEvents.remove(eventId);
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        Iterator<Map.Entry<String, EventEntry>> iterator = processedEvents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, EventEntry> entry = iterator.next();
            if (entry.getValue().updatedAt().plus(retention).isBefore(now)) {
                iterator.remove();
            }
        }
    }

    private void ensureCapacity() {
        if (processedEvents.size() < maxSize) {
            return;
        }

        String oldestKey = null;
        Instant oldestTime = null;

        for (Map.Entry<String, EventEntry> entry : processedEvents.entrySet()) {
            if (oldestTime == null || entry.getValue().updatedAt().isBefore(oldestTime)) {
                oldestTime = entry.getValue().updatedAt();
                oldestKey = entry.getKey();
            }
        }

        if (oldestKey != null) {
            processedEvents.remove(oldestKey);
        }
    }

    private record EventEntry(EventStatus status, Instant updatedAt) {
    }

    private enum EventStatus {
        PROCESSING,
        PROCESSED
    }
}