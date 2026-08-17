package com.ridex.controller;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.UpdateProfileRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.UserProfileResponse;
import com.ridex.service.UserService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<CommonApiResponse<UserProfileResponse>> getUserProfile() {

        UserProfileResponse response = userService.getProfile();

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PROFILE_FETCHED,
                response
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonApiResponse<UserProfileResponse>> updateUserProfile(
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest) {

        UserProfileResponse response = userService.updateProfile(updateProfileRequest);

        return ResponseEntity.ok(ApiResponseUtil.success(
                HttpStatus.OK.value(),
                ResponseMessage.PROFILE_UPDATED,
                response
        ));
    }
}
