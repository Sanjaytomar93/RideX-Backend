package com.ridex.serviceImpl;
import com.ridex.dto.request.UpdateProfileRequest;
import com.ridex.dto.response.UserProfileResponse;
import com.ridex.entity.User;
import com.ridex.repository.UserRepository;
import com.ridex.security.CustomUserDetails;
import com.ridex.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile() {

        User user = getCurrentUser();

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .mobileNumber(user.getMobileNumber())
                .email(user.getEmail())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .status(user.getStatus())
                .mobileVerified(user.getMobileVerified())
                .emailVerified(user.getEmailVerified())
                .profileCompleted(user.getProfileCompleted())
                .build();
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());

        user.setProfileCompleted(true);

        User updatedUser = userRepository.save(user);

        return UserProfileResponse.builder()
                .id(updatedUser.getId())
                .fullName(updatedUser.getFullName())
                .mobileNumber(updatedUser.getMobileNumber())
                .email(updatedUser.getEmail())
                .gender(updatedUser.getGender())
                .dateOfBirth(updatedUser.getDateOfBirth())
                .profileImage(updatedUser.getProfileImage())
                .role(updatedUser.getRole())
                .status(updatedUser.getStatus())
                .mobileVerified(updatedUser.getMobileVerified())
                .emailVerified(updatedUser.getEmailVerified())
                .profileCompleted(updatedUser.getProfileCompleted())
                .build();
    }

    /**
     * Get Current Logged In User
     */
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUser();
    }


}