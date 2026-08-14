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
public class DriverProfileResponse {

    private Long id;
    private String fullName;
    private String mobileNumber;
    private String licenseNumber;
    private DriverStatus status;
    private Boolean isVerified;
    private Boolean profileCompleted;
    private String rejectionReason;
    private Double rating;
    private Integer totalRides;
    private Double currentLatitude;
    private Double currentLongitude;
    private VehicleResponse vehicle;
}
