package com.ridex.exception;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.util.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseUtil.error(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleDuplicateResource(
            DuplicateResourceException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseUtil.error(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleInvalidOtp(
            InvalidOtpException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseUtil.error(
                        HttpStatus.UNAUTHORIZED.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(UserAccountInactiveException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleUserAccountInactive(
            UserAccountInactiveException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseUtil.error(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseUtil.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ResponseMessage.VALIDATION_FAILED,
                        errors
                ));
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleRideNotFound(
            RideNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseUtil.error(
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(ActiveRideExistsException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleActiveRideExists(
            ActiveRideExistsException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseUtil.error(
                        HttpStatus.CONFLICT.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(AdminAccessDeniedException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleAdminAccessDenied(
            AdminAccessDeniedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseUtil.error(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(DriverNotVerifiedException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleDriverNotVerified(
            DriverNotVerifiedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponseUtil.error(
                        HttpStatus.FORBIDDEN.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseUtil.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        List.of()
                ));
    }

    @ExceptionHandler(DriverAlreadyExistsException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleDriverAlreadyExistsException(
            DriverAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseUtil.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Driver Registration Failed",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(LicenseAlreadyExistsException.class)
    public ResponseEntity<CommonApiResponse<Object>> handleLicenseAlreadyExistsException(
            LicenseAlreadyExistsException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseUtil.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Driver Registration Failed",
                        List.of(ex.getMessage())
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<Object>> handleException(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseUtil.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ResponseMessage.INTERNAL_SERVER_ERROR,
                        List.of(ex.getMessage())
                ));
    }
}
