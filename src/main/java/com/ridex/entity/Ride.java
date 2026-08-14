package com.ridex.entity;

import com.ridex.enums.PaymentStatus;
import com.ridex.enums.RideStatus;
import com.ridex.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Ride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "pickup_latitude", nullable = false)
    private Double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private Double pickupLongitude;

    @Column(name = "drop_address", nullable = false, length = 255)
    private String dropAddress;

    @Column(name = "drop_latitude", nullable = false)
    private Double dropLatitude;

    @Column(name = "drop_longitude", nullable = false)
    private Double dropLongitude;

    @Column(name = "distance_km", nullable = false)
    private Double distanceKm;

    @Column(nullable = false)
    private Double fare;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;
}
