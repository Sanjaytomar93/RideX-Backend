package com.ridex.constants;
public class ResponseMessage {

    private ResponseMessage() {
    }

    public static final String SUCCESS = "Success";

    public static final String CREATED = "Record created successfully";

    public static final String UPDATED = "Record updated successfully";

    public static final String DELETED = "Record deleted successfully";

    public static final String FETCHED = "Data fetched successfully";

    public static final String NOT_FOUND = "Record not found";

    public static final String INTERNAL_SERVER_ERROR = "Something went wrong";

    public static final String OTP_SENT = "OTP sent successfully";

    public static final String LOGIN_SUCCESS = "Login successful";

    public static final String PROFILE_UPDATED = "Profile updated successfully";

    public static final String PROFILE_FETCHED = "Profile fetched successfully";

    public static final String INVALID_OTP = "Invalid or expired OTP";

    public static final String ACCOUNT_INACTIVE = "Account is not active";

    public static final String EMAIL_ALREADY_EXISTS = "Email is already in use";

    public static final String VALIDATION_FAILED = "Validation failed";

    public static final String DRIVER_REGISTERED = "Driver registered successfully";

    public static final String DRIVER_PROFILE_FETCHED = "Driver profile fetched successfully";

    public static final String DRIVER_STATUS_UPDATED = "Driver status updated successfully";

    public static final String DRIVER_LOCATION_UPDATED = "Driver location updated successfully";

    public static final String DRIVER_ALREADY_EXISTS = "Driver profile already exists";

    public static final String LICENSE_ALREADY_EXISTS = "License number is already registered";

    public static final String VEHICLE_ALREADY_EXISTS = "Vehicle number is already registered";

    public static final String DRIVER_NOT_FOUND = "Driver profile not found";

    public static final String DRIVER_NOT_VERIFIED = "Driver account is not verified yet";

    public static final String INVALID_DRIVER_STATUS = "Driver can only set status to ONLINE or OFFLINE";

    public static final String DRIVER_ON_RIDE = "Cannot change status while on a ride";

    public static final String DRIVER_MUST_BE_ONLINE = "Driver must be online to update location";

    public static final String DRIVER_VERIFIED = "Driver verified successfully";

    public static final String DRIVER_REJECTED = "Driver rejected successfully";

    public static final String DRIVER_ALREADY_VERIFIED = "Driver is already verified";

    public static final String DRIVER_PROFILE_INCOMPLETE = "Driver profile is not completed yet";

    public static final String PENDING_DRIVERS_FETCHED = "Pending drivers fetched successfully";

    public static final String ADMIN_ACCESS_DENIED = "Admin access required";

    public static final String RIDE_REQUESTED = "Ride requested successfully";

    public static final String RIDE_FETCHED = "Ride fetched successfully";

    public static final String RIDE_CANCELLED = "Ride cancelled successfully";

    public static final String RIDE_HISTORY_FETCHED = "Ride history fetched successfully";

    public static final String RIDE_NOT_FOUND = "Ride not found";

    public static final String ACTIVE_RIDE_EXISTS = "You already have an active ride";

    public static final String DRIVER_ACTIVE_RIDE_EXISTS = "Driver already has an active ride";

    public static final String RIDE_CANNOT_BE_CANCELLED = "Ride cannot be cancelled at this stage";

    public static final String RIDE_REQUESTS_FETCHED = "Ride requests fetched successfully";

    public static final String RIDE_ACCEPTED = "Ride accepted successfully";

    public static final String RIDE_DRIVER_ARRIVED = "Driver arrived at pickup location";

    public static final String RIDE_STARTED = "Ride started successfully";

    public static final String RIDE_COMPLETED = "Ride completed successfully";

    public static final String RIDE_NOT_AVAILABLE = "Ride is no longer available";

    public static final String RIDE_VEHICLE_TYPE_MISMATCH = "Ride vehicle type does not match your vehicle";

    public static final String INVALID_RIDE_STATUS_TRANSITION = "Invalid ride status transition";

    public static final String DRIVER_VEHICLE_NOT_FOUND = "Driver vehicle not found";
}
