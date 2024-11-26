package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.models.BranchManager;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface BranchManagerRepository extends UserRepository<BranchManager>, JpaSpecificationExecutor<BranchManager> {
}
