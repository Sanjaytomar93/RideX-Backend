package com.ridex.controller;
import com.ridex.constants.ResponseMessage;
import com.ridex.dto.request.LocationRequest;
import com.ridex.dto.response.CommonApiResponse;
import com.ridex.dto.response.LocationResponse;
import com.ridex.service.LocationService;
import com.ridex.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<CommonApiResponse<LocationResponse>> saveLocation(
            @Valid @RequestBody LocationRequest request) {

        LocationResponse response =
                locationService.saveLocation(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(
                        HttpStatus.CREATED.value(),
                        "Location saved successfully",
                        response
                ));
    }

    @GetMapping
    public ResponseEntity<CommonApiResponse<List<LocationResponse>>> getMyLocations() {

        List<LocationResponse> response =
                locationService.getMyLocations();

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        ResponseMessage.FETCHED,
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonApiResponse<LocationResponse>> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request) {

        LocationResponse response =
                locationService.updateLocation(id, request);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        ResponseMessage.UPDATED,
                        response
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonApiResponse<Boolean>> deleteLocation(
            @PathVariable Long id) {

        Boolean response =
                locationService.deleteLocation(id);

        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK.value(),
                        ResponseMessage.DELETED,
                        response
                )
        );
    }
}