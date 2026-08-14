package com.ridex.repository;

import com.ridex.entity.Driver;
import com.ridex.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByMobileNumberAndDeletedFalse(String mobileNumber);

    Optional<Driver> findByMobileNumber(String mobileNumber);

    Optional<Driver> findByIdAndDeletedFalse(Long id);

    boolean existsByMobileNumberAndDeletedFalse(String mobileNumber);

    boolean existsByLicenseNumberAndDeletedFalse(String licenseNumber);

    List<Driver> findByStatusAndDeletedFalse(DriverStatus status);

    Page<Driver> findByProfileCompletedTrueAndIsVerifiedFalseAndDeletedFalse(Pageable pageable);
}
