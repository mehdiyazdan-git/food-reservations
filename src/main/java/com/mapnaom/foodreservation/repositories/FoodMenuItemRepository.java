package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.FoodMenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodMenuItemRepository extends JpaRepository<FoodMenuItem, Integer> {
}