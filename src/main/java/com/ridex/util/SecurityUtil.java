package com.ridex.util;
import com.ridex.entity.User;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        return userDetails.getUser();
    }
}
