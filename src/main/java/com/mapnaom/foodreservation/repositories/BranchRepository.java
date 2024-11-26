package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>, JpaSpecificationExecutor<Branch> {


    @Query("select b from Branch b where b.name like concat('%', :name, '%')")
    List<Branch> findBranchByNameContains(@Param("name") String name);
}