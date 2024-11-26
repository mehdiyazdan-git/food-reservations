package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.models.Contractor;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

// Repository for Contractor
@Repository
public interface ContractorRepository extends UserRepository<Contractor>, JpaSpecificationExecutor<Contractor> {
}
