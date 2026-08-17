package com.ridex.service;

import com.ridex.entity.Ride;

public interface RideNotificationService {

    void notifyEligibleDrivers(Ride ride);
}
