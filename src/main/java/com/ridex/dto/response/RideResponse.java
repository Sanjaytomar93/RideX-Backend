package com.ridex.dto.response;

import com.ridex.enums.PaymentStatus;
import com.ridex.enums.RideStatus;
import com.ridex.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userMobile;
    private Long driverId;
    private String driverName;
    private String driverMobile;
    private VehicleType vehicleType;
    private String pickupAddress;
    private Double pickupLatitude;
    private Double pickupLongitude;
    private String dropAddress;
    private Double dropLatitude;
    private Double dropLongitude;
    private Double distanceKm;
    private Double fare;
    private RideStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
    private String cancellationReason;
}
