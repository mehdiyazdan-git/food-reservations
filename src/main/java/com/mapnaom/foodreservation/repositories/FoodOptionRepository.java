package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.FoodOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOptionRepository extends JpaRepository<FoodOption, Long>, JpaSpecificationExecutor<FoodOption> {
}