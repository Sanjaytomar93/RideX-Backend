package com.ridex.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonApiResponse<T> {

    private int status;

    private boolean success;

    private String message;

    private T data;

    private List<String> errors;

    private LocalDateTime timestamp;
}