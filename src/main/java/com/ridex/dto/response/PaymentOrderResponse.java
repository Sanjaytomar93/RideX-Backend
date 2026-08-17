package com.ridex.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {

    private Long paymentId;
    private Long rideId;
    private String razorpayOrderId;
    private String keyId;
    private BigDecimal amount;
    private String currency;
}
