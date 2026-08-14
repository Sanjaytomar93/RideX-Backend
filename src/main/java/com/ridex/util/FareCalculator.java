package com.ridex.util;

import com.ridex.enums.VehicleType;

public final class FareCalculator {

    private static final double BASE_FARE = 25.0;
    private static final double MIN_FARE = 30.0;

    private FareCalculator() {
    }

    public static double calculateFare(VehicleType vehicleType, double distanceKm) {

        double ratePerKm = switch (vehicleType) {
            case BIKE -> 8.0;
            case AUTO -> 12.0;
            case CAB -> 18.0;
        };

        double fare = BASE_FARE + (distanceKm * ratePerKm);
        return Math.round(Math.max(fare, MIN_FARE) * 100.0) / 100.0;
    }
}
