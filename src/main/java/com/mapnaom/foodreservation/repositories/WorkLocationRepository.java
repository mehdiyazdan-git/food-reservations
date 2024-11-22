package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long>, JpaSpecificationExecutor<WorkLocation> {

    Optional<WorkLocation> findByWorkLocationName(String workLocationName);
    boolean existsByWorkLocationName(String workLocationName);


}