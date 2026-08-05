package com.ridex.repository;
import com.ridex.entity.Location;
import com.ridex.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserAndDeletedFalse(User user);

    Optional<Location> findByIdAndDeletedFalse(Long id);

}