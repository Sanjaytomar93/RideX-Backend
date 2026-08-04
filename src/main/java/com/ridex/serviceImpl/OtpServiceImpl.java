package com.ridex.serviceImpl;
import com.ridex.service.OtpService;
import org.springframework.stereotype.Service;

@Service
public class OtpServiceImpl implements OtpService {

    @Override
    public boolean verifyOtp(String mobileNumber, String otp) {
        return "123456".equals(otp);
    }
}
