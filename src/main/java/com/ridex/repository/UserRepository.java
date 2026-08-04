package com.ridex.repository;
import com.ridex.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByMobileNumber(String mobileNumber);

    boolean existsByEmail(String email);

    boolean existsByMobileNumberAndDeletedFalse(String mobileNumber);

    Optional<User>  findByMobileNumber(String mobileNumber);

    Optional<User>  findByEmail(String email);

    Optional<User> findByIdAndDeletedFalse(Long id);

    Optional<User> findByMobileNumberAndDeletedFalse(String mobileNumber);
}