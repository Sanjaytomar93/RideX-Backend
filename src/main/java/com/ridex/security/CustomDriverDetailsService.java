package com.ridex.security;

import com.ridex.entity.Driver;
import com.ridex.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomDriverDetailsService implements UserDetailsService {

    private final DriverRepository driverRepository;

    @Override
    public UserDetails loadUserByUsername(String mobileNumber)
            throws UsernameNotFoundException {

        log.debug("Loading driver for mobile ending with {}", mobileNumber.substring(6));

        Driver driver = driverRepository
                .findByMobileNumberAndDeletedFalse(mobileNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Driver not found"));

        return new CustomDriverDetails(driver);
    }
}
