package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.FoodMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DailyFoodOptionsRepository extends JpaRepository<FoodMenu, Long>, JpaSpecificationExecutor<FoodMenu> {


    boolean existsByLocalDate(LocalDate date);
}