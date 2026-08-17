package com.ridex.dto.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateOrderRequest {

    @NotNull
    @Positive
    private Long rideId;
}