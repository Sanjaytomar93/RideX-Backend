package com.ridex.service;

import com.razorpay.RazorpayException;
import com.ridex.dto.request.VerifyPaymentRequest;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.PaymentOrderResponse;
import com.ridex.dto.response.PaymentResponse;
import org.springframework.data.domain.Pageable;

public interface RazorpayService {

    PaymentOrderResponse createOrder(Long rideId) throws RazorpayException;

    PaymentResponse verifyPayment(VerifyPaymentRequest request);

    PaymentResponse getPaymentForRide(Long rideId);

    PageResponse<PaymentResponse> getPaymentHistory(Pageable pageable);

    void handleWebhook(String payload, String signature);
}
