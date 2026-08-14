package com.ridex.entity;

import com.ridex.enums.DriverStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Driver extends BaseEntity {

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "mobile_number", nullable = false, unique = true, length = 10)
    private String mobileNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "license_number", unique = true, length = 20)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Builder.Default
    @Column(name = "mobile_verified", nullable = false)
    private Boolean mobileVerified = false;

    @Builder.Default
    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Builder.Default
    @Column(nullable = false)
    private Double rating = 0.0;

    @Builder.Default
    @Column(name = "total_rides", nullable = false)
    private Integer totalRides = 0;

    @Column(name = "current_latitude")
    private Double currentLatitude;

    @Column(name = "current_longitude")
    private Double currentLongitude;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vehicle vehicle;
}
