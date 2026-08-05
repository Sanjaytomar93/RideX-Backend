package com.ridex.serviceImpl;

import com.ridex.service.OtpService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();



    @Override
    public boolean verifyOtp(String mobileNumber, String otp) {

        String storedOtp = otpStorage.get(mobileNumber);

        if (storedOtp == null) {
            return false;
        }

        if (!storedOtp.equals(otp)) {
            return false;
        }

        otpStorage.remove(mobileNumber);

        return true;
    }

    @Override
    public void sendOtsp(String mobileNumber) {

        String otp = String.format("%06d", new Random().nextInt(1000000));

        otpStorage.put(mobileNumber, otp);

        System.out.println("--------------------------------");
        System.out.println("Mobile : " + mobileNumber);
        System.out.println("OTP    : " + otp);
        System.out.println("--------------------------------");

    }
}