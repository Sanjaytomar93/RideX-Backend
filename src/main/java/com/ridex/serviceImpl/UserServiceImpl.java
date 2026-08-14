package com.ridex.serviceImpl;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.UpdateProfileRequest;
import com.ridex.dto.response.UserProfileResponse;
import com.ridex.entity.User;
import com.ridex.exception.DuplicateResourceException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.repository.UserRepository;
import com.ridex.service.UserService;
import com.ridex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile() {

        User user = getFreshUserFromDb();

        return mapToResponse(user);
    }

    @Override
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {

        User user = getFreshUserFromDb();

        if (StringUtils.hasText(request.getEmail())
                && !request.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
            throw new DuplicateResourceException(ResponseMessage.EMAIL_ALREADY_EXISTS);
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setProfileImage(request.getProfileImage());
        user.setProfileCompleted(true);

        if (StringUtils.hasText(request.getEmail())
                && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            user.setEmailVerified(false);
        }

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    private User getFreshUserFromDb() {

        User currentUser = SecurityUtil.getCurrentUser();

        return userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserProfileResponse mapToResponse(User user) {

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
}
