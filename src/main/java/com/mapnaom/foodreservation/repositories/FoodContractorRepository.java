package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.FoodContractor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FoodContractorRepository extends JpaRepository<FoodContractor, Integer>, JpaSpecificationExecutor<FoodContractor> {

    @Query("select fc from FoodContractor fc where fc.name like %:name%")
    Optional<FoodContractor> findByNameContaining(@Param("name") String name);
}