package notificationservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import notificationservice.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Глобальный обработчик исключений REST API notification-service.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Обрабатывает ошибки bean validation.
     *
     * @param ex исключение валидации
     * @param request текущий HTTP-запрос
     * @return ответ со статусом 400 и деталями ошибок
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                             HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации входных данных.",
                request,
                details
        );
    }

    /**
     * Обрабатывает ошибки чтения тела запроса, например некорректный JSON или неверное значение enum.
     *
     * @param ex исключение чтения HTTP-тела
     * @param request текущий HTTP-запрос
     * @return ответ со статусом 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex,
                                                                    HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Некорректный формат тела запроса.",
                request,
                List.of()
        );
    }

    /**
     * Обрабатывает ошибки доставки уведомления во внешний канал.
     *
     * @param ex исключение доставки уведомления
     * @param request текущий HTTP-запрос
     * @return ответ со статусом 503
     */
    @ExceptionHandler(NotificationDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleDeliveryException(NotificationDeliveryException ex,
                                                                    HttpServletRequest request) {
        log.error("Внешний канал уведомлений недоступен для запроса {}", request.getRequestURI(), ex);
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Сервис отправки уведомлений временно недоступен.",
                request,
                List.of()
        );
    }

    /**
     * Обрабатывает все непредвиденные ошибки приложения.
     *
     * @param ex исходное исключение
     * @param request текущий HTTP-запрос
     * @return ответ со статусом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex,
                                                             HttpServletRequest request) {
        log.error("Непредвиденная ошибка при обработке запроса {}", request.getRequestURI(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера.",
                request,
                List.of()
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                           String message,
                                                           HttpServletRequest request,
                                                           List<String> details) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}