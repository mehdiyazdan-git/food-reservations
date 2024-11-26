package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.models.SystemAdmin;
import org.springframework.stereotype.Repository;

// Repository for SystemAdmin
@Repository
public interface SystemAdminRepository extends UserRepository<SystemAdmin> {
}