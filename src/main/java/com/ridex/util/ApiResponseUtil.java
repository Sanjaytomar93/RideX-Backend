package com.ridex.util;
import com.ridex.dto.response.CommonApiResponse;
import java.time.LocalDateTime;
import java.util.List;

public class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static <T> CommonApiResponse<T> success(int status,
                                                   String message,
                                                   T data) {

        return CommonApiResponse.<T>builder()
                .status(status)
                .success(true)
                .message(message)
                .data(data)
                .errors(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> CommonApiResponse<T> error(int status,
                                                 String message,
                                                 List<String> errors) {

        return CommonApiResponse.<T>builder()
                .status(status)
                .success(false)
                .message(message)
                .data(null)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}