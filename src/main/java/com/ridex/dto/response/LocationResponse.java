package com.ridex.dto.response;
import com.ridex.enums.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {

    private Long id;

    private LocationType type;

    private String address;

    private Double latitude;

    private Double longitude;

    private Boolean isDefault;

}