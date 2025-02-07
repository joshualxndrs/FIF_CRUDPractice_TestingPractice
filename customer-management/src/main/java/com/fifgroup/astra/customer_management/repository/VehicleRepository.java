package com.fifgroup.astra.customer_management.repository;

import com.fifgroup.astra.customer_management.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByOwner_Id(Long userId);

    Optional<Vehicle> findByIdAndOwner_Id(Long id, Long userId);
}
