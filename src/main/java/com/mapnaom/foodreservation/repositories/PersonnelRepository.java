package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long>, JpaSpecificationExecutor<Personnel> {

    @Query("select (count(p) > 0) from Personnel p where p.code = :code")
    boolean existsByCode(@Param("code") String code);
}