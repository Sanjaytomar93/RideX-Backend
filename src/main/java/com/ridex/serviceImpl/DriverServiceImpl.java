package com.ridex.serviceImpl;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.DriverRegistrationRequest;
import com.ridex.dto.request.UpdateDriverLocationRequest;
import com.ridex.dto.request.UpdateDriverStatusRequest;
import com.ridex.dto.response.DriverProfileResponse;
import com.ridex.dto.response.VehicleResponse;
import com.ridex.entity.Driver;
import com.ridex.entity.Vehicle;
import com.ridex.enums.DriverStatus;
import com.ridex.exception.DriverAlreadyExistsException;
import com.ridex.exception.DriverNotVerifiedException;
import com.ridex.exception.DuplicateResourceException;
import com.ridex.exception.LicenseAlreadyExistsException;
import com.ridex.exception.ResourceNotFoundException;
import com.ridex.repository.DriverRepository;
import com.ridex.repository.VehicleRepository;
import com.ridex.service.DriverService;
import com.ridex.util.DriverSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public DriverProfileResponse register(DriverRegistrationRequest request) {

        Driver driver = getFreshDriverFromDb();

        if (Boolean.TRUE.equals(driver.getProfileCompleted())) {
            throw new DriverAlreadyExistsException(ResponseMessage.DRIVER_ALREADY_EXISTS);
        }

        if (driverRepository.existsByLicenseNumberAndDeletedFalse(request.getLicenseNumber())) {
            throw new LicenseAlreadyExistsException(ResponseMessage.LICENSE_ALREADY_EXISTS);
        }

        if (vehicleRepository.existsByVehicleNumberAndDeletedFalse(request.getVehicleNumber())) {
            throw new DuplicateResourceException(ResponseMessage.VEHICLE_ALREADY_EXISTS);
        }

        driver.setFullName(request.getFullName());
        driver.setLicenseNumber(request.getLicenseNumber().toUpperCase());
        driver.setProfileCompleted(true);
        driver.setStatus(DriverStatus.INACTIVE);
        driver.setIsVerified(false);

        Vehicle vehicle = Vehicle.builder()
                .driver(driver)
                .vehicleType(request.getVehicleType())
                .vehicleNumber(request.getVehicleNumber().toUpperCase())
                .model(request.getModel())
                .color(request.getColor())
                .build();

        driver.setVehicle(vehicle);

        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverProfileResponse getProfile() {

        Driver driver = getFreshDriverFromDb();
        return mapToResponse(driver);
    }

    @Override
    @Transactional
    public DriverProfileResponse updateStatus(UpdateDriverStatusRequest request) {

        Driver driver = getFreshDriverFromDb();

        if (request.getStatus() != DriverStatus.ONLINE
                && request.getStatus() != DriverStatus.OFFLINE) {
            throw new IllegalArgumentException(ResponseMessage.INVALID_DRIVER_STATUS);
        }

        if (!Boolean.TRUE.equals(driver.getIsVerified())) {
            throw new DriverNotVerifiedException(ResponseMessage.DRIVER_NOT_VERIFIED);
        }

        if (driver.getStatus() == DriverStatus.ON_RIDE) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_ON_RIDE);
        }

        driver.setStatus(request.getStatus());

        if (request.getStatus() == DriverStatus.OFFLINE) {
            driver.setCurrentLatitude(null);
            driver.setCurrentLongitude(null);
        }

        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    @Override
    @Transactional
    public DriverProfileResponse updateLocation(UpdateDriverLocationRequest request) {

        Driver driver = getFreshDriverFromDb();

        if (driver.getStatus() != DriverStatus.ONLINE) {
            throw new IllegalArgumentException(ResponseMessage.DRIVER_MUST_BE_ONLINE);
        }

        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());

        Driver updatedDriver = driverRepository.save(driver);

        return mapToResponse(updatedDriver);
    }

    private Driver getFreshDriverFromDb() {

        Driver currentDriver = DriverSecurityUtil.getCurrentDriver();

        return driverRepository.findByIdAndDeletedFalse(currentDriver.getId())
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
