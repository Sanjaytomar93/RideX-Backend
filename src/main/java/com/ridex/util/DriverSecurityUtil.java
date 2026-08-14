package com.ridex.util;

import com.ridex.entity.Driver;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.security.CustomDriverDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class DriverSecurityUtil {

    private DriverSecurityUtil() {
    }

    public static Driver getCurrentDriver() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof CustomDriverDetails driverDetails)) {
            throw new ResourceNotFoundException("Authenticated driver not found");
        }

        return driverDetails.getDriver();
    }
}
