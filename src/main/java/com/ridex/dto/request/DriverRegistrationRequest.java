package com.ridex.dto.request;
import com.ridex.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class DriverRegistrationRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "License number is required")
    @Pattern(
            regexp = "^[A-Z0-9]{5,20}$",
            message = "Invalid license number format"
    )
    private String licenseNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotBlank(message = "Vehicle number is required")
    @Pattern(
            regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$",
            message = "Invalid vehicle number format"
    )
    private String vehicleNumber;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @NotBlank(message = "Vehicle color is required")
    private String color;
}
