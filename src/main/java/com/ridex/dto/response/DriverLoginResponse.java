package com.ridex.dto.response;

import com.ridex.enums.DriverStatus;
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
public class DriverLoginResponse {

    private String accessToken;

    private String tokenType;

    private Long driverId;

    private String mobileNumber;

    private Boolean profileCompleted;

    private Boolean isVerified;

    private DriverStatus status;
}
