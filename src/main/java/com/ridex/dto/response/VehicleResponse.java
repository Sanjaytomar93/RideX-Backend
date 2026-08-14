package com.ridex.dto.response;

import com.ridex.enums.DriverStatus;
import com.ridex.enums.VehicleType;
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
public class VehicleResponse {

    private Long id;
    private VehicleType vehicleType;
    private String vehicleNumber;
    private String model;
    private String color;
}
