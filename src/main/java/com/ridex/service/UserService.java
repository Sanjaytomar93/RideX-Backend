package com.ridex.service;
import com.ridex.dto.request.UpdateProfileRequest;
import com.ridex.dto.response.UserProfileResponse;

public interface UserService {

    UserProfileResponse getProfile();

    UserProfileResponse updateProfile(UpdateProfileRequest request);

}