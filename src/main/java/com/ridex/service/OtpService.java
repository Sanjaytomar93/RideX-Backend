package com.ridex.service;

public interface OtpService {

    boolean verifyOtp(String mobileNumber,
                      String otp);
}
