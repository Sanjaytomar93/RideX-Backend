package com.ridex.serviceImpl;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.SendOtpRequest;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.DriverLoginResponse;
import com.ridex.entity.Driver;
import com.ridex.enums.DriverStatus;
import com.ridex.exception.InvalidOtpException;
import com.ridex.repository.DriverRepository;
import com.ridex.security.JwtService;
import com.ridex.service.DriverAuthService;
import com.ridex.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverAuthServiceImpl implements DriverAuthService {

    private final DriverRepository driverRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Override
    public void sendOtp(SendOtpRequest request) {
        otpService.sendOtp(request.getMobileNumber());
    }

    @Override
    public DriverLoginResponse verifyOtp(VerifyOtpRequest request) {

        boolean isOtpValid = otpService.verifyOtp(
                request.getMobileNumber(),
                request.getOtp()
        );

        if (!isOtpValid) {
            throw new InvalidOtpException(ResponseMessage.INVALID_OTP);
        }

        Driver driver = findOrCreateDriver(request.getMobileNumber());

        if (driver.getStatus() == DriverStatus.BLOCKED) {
            throw new InvalidOtpException(ResponseMessage.ACCOUNT_INACTIVE);
        }

        String accessToken = jwtService.generateToken(driver);

        return DriverLoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .driverId(driver.getId())
                .mobileNumber(driver.getMobileNumber())
                .profileCompleted(driver.getProfileCompleted())
                .isVerified(driver.getIsVerified())
                .status(driver.getStatus())
                .build();
    }

    private Driver findOrCreateDriver(String mobileNumber) {

        return driverRepository.findByMobileNumberAndDeletedFalse(mobileNumber)
                .orElseGet(() -> driverRepository.findByMobileNumber(mobileNumber)
                        .filter(Driver::getDeleted)
                        .map(this::reactivateDriver)
                        .orElseGet(() -> createNewDriver(mobileNumber)));
    }

    private Driver reactivateDriver(Driver driver) {

        driver.setDeleted(false);
        driver.setStatus(DriverStatus.INACTIVE);
        driver.setMobileVerified(true);

        return driverRepository.save(driver);
    }

    private Driver createNewDriver(String mobileNumber) {

        Driver driver = Driver.builder()
                .mobileNumber(mobileNumber)
                .status(DriverStatus.INACTIVE)
                .isVerified(false)
                .mobileVerified(true)
                .profileCompleted(false)
                .rating(0.0)
                .totalRides(0)
                .build();

        return driverRepository.save(driver);
    }
}
