package com.ridex.serviceImpl;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.LoginResponse;
import com.ridex.entity.User;
import com.ridex.enums.Role;
import com.ridex.enums.UserStatus;
import com.ridex.exception.InvalidOtpException;
import com.ridex.exception.UserAccountInactiveException;
import com.ridex.repository.UserRepository;
import com.ridex.security.JwtService;
import com.ridex.service.AuthService;
import com.ridex.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Override
    public void sendOtp(String mobileNumber) {
        otpService.sendOtp(mobileNumber);
    }

    @Override
    public LoginResponse verifyOtp(VerifyOtpRequest request) {

        boolean isOtpValid = otpService.verifyOtp(
                request.getMobileNumber(),
                request.getOtp()
        );

        if (!isOtpValid) {
            throw new InvalidOtpException(ResponseMessage.INVALID_OTP);
        }

        User user = findOrCreateUser(request);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserAccountInactiveException(ResponseMessage.ACCOUNT_INACTIVE);
        }

        String accessToken = jwtService.generateToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .role(user.getRole())
                .profileCompleted(user.getProfileCompleted())
                .build();
    }

    private User findOrCreateUser(VerifyOtpRequest request) {

        return userRepository
                .findByMobileNumberAndDeletedFalse(request.getMobileNumber())
                .orElseGet(() -> userRepository
                        .findByMobileNumber(request.getMobileNumber())
                        .filter(User::getDeleted)
                        .map(this::reactivateUser)
                        .orElseGet(() -> createNewUser(request)));
    }

    private User reactivateUser(User user) {

        user.setDeleted(false);
        user.setStatus(UserStatus.ACTIVE);
        user.setMobileVerified(true);

        return userRepository.save(user);
    }

    private User createNewUser(VerifyOtpRequest request) {

        User user = User.builder()
                .mobileNumber(request.getMobileNumber())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .mobileVerified(true)
                .emailVerified(false)
                .profileCompleted(false)
                .build();

        return userRepository.save(user);
    }
}
