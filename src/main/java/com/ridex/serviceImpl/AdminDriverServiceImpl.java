package com.ridex.serviceImpl;

import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.RejectDriverRequest;
import com.ridex.dto.response.DriverProfileResponse;
import com.ridex.dto.response.PageResponse;
import com.ridex.dto.response.VehicleResponse;
import com.ridex.entity.Driver;
import com.ridex.entity.User;
import com.ridex.entity.Vehicle;
import com.ridex.enums.DriverStatus;
import com.ridex.enums.Role;
import com.ridex.exception.AdminAccessDeniedException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.repository.DriverRepository;
import com.ridex.repository.UserRepository;
import com.ridex.service.AdminDriverService;
import com.ridex.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDriverServiceImpl implements AdminDriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DriverProfileResponse> getPendingDrivers(Pageable pageable) {

        ensureAdmin();

        Page<Driver> driverPage = driverRepository
                .findByProfileCompletedTrueAndIsVerifiedFalseAndDeletedFalse(pageable);

        return PageResponse.<DriverProfileResponse>builder()
                .content(driverPage.getContent().stream().map(this::mapToResponse).toList())
                .page(driverPage.getNumber())
                .size(driverPage.getSize())
                .totalElements(driverPage.getTotalElements())
                .totalPages(driverPage.getTotalPages())
                .last(driverPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DriverProfileResponse getDriverById(Long driverId) {

        ensureAdmin();

        Driver driver = getDriverOrThrow(driverId);
        return mapToResponse(driver);
    }

    @Override
    @Transactional
    public DriverProfileResponse verifyDriver(Long driverId) {

        ensureAdmin();

        Driver driver = getDriverOrThrow(driverId);

        if (!Boolean.TRUE.equals(driver.getProfileCompleted())) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_PROFILE_INCOMPLETE);
        }

        if (Boolean.TRUE.equals(driver.getIsVerified())) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_ALREADY_VERIFIED);
        }

        driver.setIsVerified(true);
        driver.setStatus(DriverStatus.OFFLINE);
        driver.setRejectionReason(null);

        Driver verifiedDriver = driverRepository.save(driver);

        return mapToResponse(verifiedDriver);
    }

    @Override
    @Transactional
    public DriverProfileResponse rejectDriver(Long driverId, RejectDriverRequest request) {

        ensureAdmin();

        Driver driver = getDriverOrThrow(driverId);

        if (Boolean.TRUE.equals(driver.getIsVerified())) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_ALREADY_VERIFIED);
        }

        driver.setIsVerified(false);
        driver.setStatus(DriverStatus.BLOCKED);
        driver.setRejectionReason(request.getReason());

        Driver rejectedDriver = driverRepository.save(driver);

        return mapToResponse(rejectedDriver);
    }

    private void ensureAdmin() {

        User currentUser = SecurityUtil.getCurrentUser();

        User admin = userRepository.findByIdAndDeletedFalse(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new AdminAccessDeniedException(ResponseMessage.ADMIN_ACCESS_DENIED);
        }
    }

    private Driver getDriverOrThrow(Long driverId) {

        return driverRepository.findByIdAndDeletedFalse(driverId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.DRIVER_NOT_FOUND));
    }

    private DriverProfileResponse mapToResponse(Driver driver) {

        Vehicle vehicle = driver.getVehicle();

        VehicleResponse vehicleResponse = null;

        if (vehicle != null) {
            vehicleResponse = VehicleResponse.builder()
                    .id(vehicle.getId())
                    .vehicleType(vehicle.getVehicleType())
                    .vehicleNumber(vehicle.getVehicleNumber())
                    .model(vehicle.getModel())
                    .color(vehicle.getColor())
                    .build();
        }

        return DriverProfileResponse.builder()
                .id(driver.getId())
                .fullName(driver.getFullName())
                .mobileNumber(driver.getMobileNumber())
                .licenseNumber(driver.getLicenseNumber())
                .status(driver.getStatus())
                .isVerified(driver.getIsVerified())
                .profileCompleted(driver.getProfileCompleted())
                .rejectionReason(driver.getRejectionReason())
                .rating(driver.getRating())
                .totalRides(driver.getTotalRides())
                .currentLatitude(driver.getCurrentLatitude())
                .currentLongitude(driver.getCurrentLongitude())
                .vehicle(vehicleResponse)
                .build();
    }
}
