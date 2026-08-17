package com.ridex.controller;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.VerifyPaymentRequest;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.PaymentOrderResponse;
import com.ridex.dto.response.PaymentResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ridex.dto.request.CreateOrderRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.service.RazorpayService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<CommonApiResponse<PaymentOrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) throws com.razorpay.RazorpayException {

        PaymentOrderResponse response = razorpayService.createOrder(request.getRideId());

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        ResponseMessage.PAYMENT_ORDER_CREATED,
                        response));
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonApiResponse<PaymentResponse>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        PaymentResponse response = razorpayService.verifyPayment(request);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PAYMENT_VERIFIED,
                response));
    }

    @PostMapping("/webhook")
    public ResponseEntity<CommonApiResponse<Void>> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {

        razorpayService.handleWebhook(payload, signature);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.WEBHOOK_PROCESSED,
                null));
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<CommonApiResponse<PaymentResponse>> getPaymentForRide(
            @PathVariable Long rideId) {

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PAYMENT_FETCHED,
                razorpayService.getPaymentForRide(rideId)));
    }

    @GetMapping("/history")
    public ResponseEntity<CommonApiResponse<PageResponse<PaymentResponse>>> getPaymentHistory(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PAYMENT_HISTORY_FETCHED,
                razorpayService.getPaymentHistory(pageable)));
    }
}
