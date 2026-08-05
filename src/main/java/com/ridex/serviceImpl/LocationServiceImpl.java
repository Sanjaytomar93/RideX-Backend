package com.ridex.serviceImpl;
import com.ridex.dto.request.LocationRequest;
import com.ridex.dto.response.LocationResponse;
import com.ridex.entity.Location;
import com.ridex.entity.User;
import com.ridex.repository.LocationRepository;
import com.ridex.security.CustomUserDetails;
import com.ridex.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationResponse saveLocation(LocationRequest request) {

        User user = getCurrentUser();

        Location location = new Location();

        location.setUser(user);
        location.setType(request.getType());
        location.setAddress(request.getAddress());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setIsDefault(request.getIsDefault());

        Location savedLocation = locationRepository.save(location);

        return mapToResponse(savedLocation);
    }

    @Override
    public List<LocationResponse> getMyLocations() {

        User user = getCurrentUser();

        List<Location> locations =
                locationRepository.findByUserAndDeletedFalse(user);

        return locations.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public LocationResponse updateLocation(Long locationId,
                                           LocationRequest request) {

        User user = getCurrentUser();

        Location location = locationRepository
                .findByIdAndDeletedFalse(locationId)
                .orElseThrow(() ->
                        new RuntimeException("Location not found"));

        // Security Check
        if (!location.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this location");
        }

        location.setType(request.getType());
        location.setAddress(request.getAddress());
        location.setLatitude(request.getLatitude());
        location.setLongitude(request.getLongitude());
        location.setIsDefault(request.getIsDefault());

        Location updatedLocation = locationRepository.save(location);

        return mapToResponse(updatedLocation);
    }

    @Override
    public Boolean deleteLocation(Long locationId) {

        User user = getCurrentUser();

        Location location = locationRepository
                .findByIdAndDeletedFalse(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        if (!location.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized");
        }

        location.setDeleted(true);
        locationRepository.save(location);

        return true;
    }

    /**
     * Get Current Login User
     */
    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUser();
    }

    /**
     * Entity -> Response DTO
     */
    private LocationResponse mapToResponse(Location location) {

        return LocationResponse.builder()
                .id(location.getId())
                .type(location.getType())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .isDefault(location.getIsDefault())
                .build();
    }
}