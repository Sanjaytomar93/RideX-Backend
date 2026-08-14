package com.ridex.serviceImpl;
import com.ridex.service.OtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpServiceImpl implements OtpService {

    private static final long OTP_EXPIRY_SECONDS = 300;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    @Override
    public boolean verifyOtp(String mobileNumber, String otp) {

        OtpEntry entry = otpStorage.get(mobileNumber);

        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            otpStorage.remove(mobileNumber);
            return false;
        }

        if (!entry.otp().equals(otp)) {
            return false;
        }

        otpStorage.remove(mobileNumber);
        return true;
    }

    @Override
    public void sendOtp(String mobileNumber) {

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        otpStorage.put(
                mobileNumber,
                new OtpEntry(otp, Instant.now().plusSeconds(OTP_EXPIRY_SECONDS))
        );

        log.info("OTP generated for mobile ending with {}", mobileNumber.substring(6));
        log.info("OTP for {}: {}", mobileNumber, otp);
    }

    private record OtpEntry(String otp, Instant expiresAt) {
    }
}
