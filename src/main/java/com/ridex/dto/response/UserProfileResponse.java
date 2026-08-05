package com.ridex.dto.response;
import com.ridex.enums.Gender;
import com.ridex.enums.Role;
import com.ridex.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

    private String fullName;

    private String mobileNumber;

    private String email;

    private Gender gender;

    private LocalDate dateOfBirth;

    private String profileImage;

    private Role role;

    private UserStatus status;

    private Boolean mobileVerified;

    private Boolean emailVerified;

    private Boolean profileCompleted;

}