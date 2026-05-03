package notificationservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import notificationservice.dto.ApiErrorResponse;
import notificationservice.service.NotificationMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

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
                NotificationMessageConstants.VALIDATION_ERROR,
                request.getRequestURI(),
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestBody(HttpMessageNotReadableException ex,
                                                                     HttpServletRequest request) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                NotificationMessageConstants.VALIDATION_ERROR,
                request.getRequestURI(),
                List.of(NotificationMessageConstants.INVALID_REQUEST_BODY)
        );
    }

    @ExceptionHandler(NotificationDeliveryException.class)
    public ResponseEntity<ApiErrorResponse> handleNotificationDelivery(NotificationDeliveryException ex,
                                                                       HttpServletRequest request) {
        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                request.getRequestURI(),
                List.of()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                                  HttpServletRequest request) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Ресурс не найден.",
                request.getRequestURI(),
                List.of(ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex,
                                                             HttpServletRequest request) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                NotificationMessageConstants.UNEXPECTED_ERROR,
                request.getRequestURI(),
                List.of(ex.getMessage())
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status,
                                                           String message,
                                                           String path,
                                                           List<String> details) {
        ApiErrorResponse response = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}