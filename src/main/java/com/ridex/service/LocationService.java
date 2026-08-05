package com.ridex.service;
import com.ridex.dto.request.LocationRequest;
import com.ridex.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {

    LocationResponse saveLocation(LocationRequest request);

    List<LocationResponse> getMyLocations();

    LocationResponse updateLocation(Long id, LocationRequest request);

    Boolean deleteLocation(Long id);

}
