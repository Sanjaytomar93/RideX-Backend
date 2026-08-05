package com.ridex.serviceImpl;
import com.ridex.dto.request.VerifyOtpRequest;
import com.ridex.dto.response.LoginResponse;
import com.ridex.entity.User;
import com.ridex.enums.Role;
import com.ridex.enums.UserStatus;
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

        otpService.sendOtsp(mobileNumber);

    }

    @Override
    public LoginResponse verifyOtp(VerifyOtpRequest request) {

        // Step 1 : Verify OTP
        boolean isOtpValid = otpService.verifyOtp(
                request.getMobileNumber(),
                request.getOtp()
        );

        if (!isOtpValid) {
            throw new RuntimeException("Invalid OTP");
        }

        // Step 2 : Find Existing User
        User user = userRepository
                .findByMobileNumberAndDeletedFalse(request.getMobileNumber())
                .orElseGet(() -> createNewUser(request));

        // Step 3 : Generate JWT
        String accessToken = jwtService.generateToken(user);

        // Step 4 : Check Profile Completion
        boolean profileCompleted =
                user.getFullName() != null &&
                        !user.getFullName().isBlank();

        // Step 5 : Return Response
        return LoginResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .role(user.getRole())
                .profileCompleted(profileCompleted)
                .build();
    }

    /**
     * Create New User
     */
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