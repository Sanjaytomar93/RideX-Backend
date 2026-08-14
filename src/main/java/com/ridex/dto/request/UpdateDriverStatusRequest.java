package com.ridex.dto.request;

import com.ridex.enums.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDriverStatusRequest {

    @NotNull(message = "Driver status is required")
    private DriverStatus status;
}
