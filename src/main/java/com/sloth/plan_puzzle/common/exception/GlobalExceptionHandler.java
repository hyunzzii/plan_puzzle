package com.sloth.plan_puzzle.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    protected ResponseEntity<ErrorResponseBody> handleCustomException(HttpServletRequest request, CustomException ex) {
        CustomExceptionInfo customErrorInfo = ex.getCustomExceptionInfo();
        ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
                .detailStatusCode(customErrorInfo.getStatusCode())
                .message(customErrorInfo.getMessage())
                .build();

        return ResponseEntity
                .status(customErrorInfo.getStatusCode())
                .body(errorResponseBody);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity handleValidationExceptions(HttpServletRequest request, Exception ex) {
        return ResponseEntity
                .status(500)
                .body(Map.of("message", ex.getMessage()));
    }
}
