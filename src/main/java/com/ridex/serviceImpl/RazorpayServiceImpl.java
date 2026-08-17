package com.ridex.serviceImpl;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.VerifyPaymentRequest;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.PaymentOrderResponse;
import com.ridex.dto.response.PaymentResponse;
import com.ridex.entity.Payment;
import com.ridex.entity.Ride;
import com.ridex.entity.User;
import com.ridex.enums.PaymentStatus;
import com.ridex.enums.RideStatus;
import com.ridex.exception.PaymentNotFoundException;
import com.ridex.exception.PaymentVerificationException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.repository.PaymentRepository;
import com.ridex.repository.RideRepository;
import com.ridex.repository.UserRepository;
import com.ridex.service.RazorpayService;
import com.ridex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RazorpayServiceImpl implements RazorpayService {

    private static final String CURRENCY = "INR";

    private final RazorpayClient razorpayClient;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Value("${razorpay.webhook.secret:}")
    private String razorpayWebhookSecret;

    @Override
    @Transactional
    public PaymentOrderResponse createOrder(Long rideId) throws RazorpayException {

        User user = getFreshUserFromDb();
        Ride ride = getOwnedCompletedRide(rideId, user);

        if (ride.getPaymentStatus() == PaymentStatus.SUCCESS) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_ALREADY_PAID);
        }

        Payment payment = paymentRepository
                .findFirstByRideIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                        ride.getId(), PaymentStatus.PENDING)
                .orElseGet(() -> createRazorpayOrder(ride, user));

        return mapToOrderResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {

        User user = getFreshUserFromDb();
        Payment payment = paymentRepository
                .findByRazorpayOrderIdAndDeletedFalse(request.getRazorpayOrderId())
                .orElseThrow(() -> new PaymentNotFoundException(ResponseMessage.PAYMENT_NOT_FOUND));

        if (!payment.getUser().getId().equals(user.getId())) {
            throw new PaymentNotFoundException(ResponseMessage.PAYMENT_NOT_FOUND);
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return mapToResponse(payment);
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", request.getRazorpayOrderId());
        attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
        attributes.put("razorpay_signature", request.getRazorpaySignature());

        try {
            if (!Utils.verifyPaymentSignature(attributes, razorpayKeySecret)) {
                throw new PaymentVerificationException(ResponseMessage.PAYMENT_VERIFICATION_FAILED);
            }
        } catch (RazorpayException ex) {
            throw new PaymentVerificationException(ResponseMessage.PAYMENT_VERIFICATION_FAILED);
        }

        Ride ride = payment.getRide();
        if (ride.getStatus() != RideStatus.RIDE_COMPLETED) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_NOT_COMPLETED);
        }

        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setStatus(PaymentStatus.SUCCESS);
        ride.setPaymentStatus(PaymentStatus.SUCCESS);

        return mapToResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentForRide(Long rideId) {

        User user = getFreshUserFromDb();
        getOwnedRide(rideId, user);

        Payment payment = paymentRepository
                .findFirstByRideIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                        rideId, PaymentStatus.SUCCESS)
                .orElseGet(() -> paymentRepository
                        .findFirstByRideIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                                rideId, PaymentStatus.PENDING)
                        .orElseThrow(() -> new PaymentNotFoundException(ResponseMessage.PAYMENT_NOT_FOUND)));

        return mapToResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PaymentResponse> getPaymentHistory(Pageable pageable) {

        User user = getFreshUserFromDb();
        Page<Payment> paymentPage = paymentRepository
                .findByUserIdAndDeletedFalseOrderByCreatedAtDesc(user.getId(), pageable);

        return PageResponse.<PaymentResponse>builder()
                .content(paymentPage.getContent().stream().map(this::mapToResponse).toList())
                .page(paymentPage.getNumber())
                .size(paymentPage.getSize())
                .totalElements(paymentPage.getTotalElements())
                .totalPages(paymentPage.getTotalPages())
                .last(paymentPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String signature) {

        if (razorpayWebhookSecret.isBlank()) {
            throw new PaymentVerificationException(ResponseMessage.WEBHOOK_SECRET_NOT_CONFIGURED);
        }

        try {
            if (!Utils.verifyWebhookSignature(payload, signature, razorpayWebhookSecret)) {
                throw new PaymentVerificationException(ResponseMessage.WEBHOOK_VERIFICATION_FAILED);
            }
        } catch (RazorpayException ex) {
            throw new PaymentVerificationException(ResponseMessage.WEBHOOK_VERIFICATION_FAILED);
        }

        JSONObject webhook = new JSONObject(payload);
        JSONObject paymentEntity = webhook.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayOrderId = paymentEntity.optString("order_id");
        if (razorpayOrderId.isBlank()) {
            return;
        }

        Payment payment = paymentRepository
                .findByRazorpayOrderIdAndDeletedFalse(razorpayOrderId)
                .orElseThrow(() -> new PaymentNotFoundException(ResponseMessage.PAYMENT_NOT_FOUND));

        String event = webhook.optString("event");
        String paymentId = paymentEntity.optString("id", null);

        if ("payment.captured".equals(event)) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.getRide().setPaymentStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
        } else if ("payment.failed".equals(event)) {
            payment.setRazorpayPaymentId(paymentId);
            payment.setStatus(PaymentStatus.FAILED);
            payment.getRide().setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        } else if ("refund.created".equals(event)) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.getRide().setPaymentStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);
        }
    }

    private Payment createRazorpayOrder(Ride ride, User user) {
        try {
            BigDecimal amount = BigDecimal.valueOf(ride.getFare());
            int amountInPaise = amount.movePointRight(2).intValueExact();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", CURRENCY);
            orderRequest.put("receipt", "ride_" + ride.getId() + "_" + System.currentTimeMillis());

            Order order = razorpayClient.orders.create(orderRequest);

            Payment payment = new Payment();
            payment.setRide(ride);
            payment.setUser(user);
            payment.setAmount(amount);
            payment.setCurrency(CURRENCY);
            payment.setRazorpayOrderId(order.get("id"));
            payment.setStatus(PaymentStatus.PENDING);

            return paymentRepository.save(payment);
        } catch (RazorpayException ex) {
            throw new PaymentVerificationException(ResponseMessage.PAYMENT_ORDER_CREATION_FAILED);
        }
    }

    private Ride getOwnedCompletedRide(Long rideId, User user) {
        Ride ride = getOwnedRide(rideId, user);
        if (ride.getStatus() != RideStatus.RIDE_COMPLETED) {
            throw new IllegalArgumentException(ResponseMessage.RIDE_NOT_COMPLETED);
        }
        return ride;
    }

    private Ride getOwnedRide(Long rideId, User user) {
        Ride ride = rideRepository.findByIdAndDeletedFalse(rideId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.RIDE_NOT_FOUND));

        if (!ride.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException(ResponseMessage.RIDE_NOT_FOUND);
        }

        return ride;
    }

    private User getFreshUserFromDb() {
        User currentUser = SecurityUtil.getCurrentUser();
        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private PaymentOrderResponse mapToOrderResponse(Payment payment) {
        return PaymentOrderResponse.builder()
                .paymentId(payment.getId())
                .rideId(payment.getRide().getId())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .keyId(razorpayKeyId)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .build();
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRide().getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .razorpayOrderId(payment.getRazorpayOrderId())
                .razorpayPaymentId(payment.getRazorpayPaymentId())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
