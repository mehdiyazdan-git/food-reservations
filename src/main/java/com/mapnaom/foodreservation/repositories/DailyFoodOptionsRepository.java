package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyFoodOptionsRepository extends JpaRepository<DailyFoodOptions, Long>, JpaSpecificationExecutor<DailyFoodOptions> {


    boolean existsByLocalDate(LocalDate date);
}