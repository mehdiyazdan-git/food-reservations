package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long>, JpaSpecificationExecutor<Food> {

    @Query("select (count(f) > 0) from Food f where f.name = :name")
    boolean existsByName(@Param("name") String name);
}