package com.mapnaom.foodreservation.repositories;

import com.mapnaom.foodreservation.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository for User (Base Repository for common user operations)
@Repository
public interface UserRepository<T extends User> extends JpaRepository<T, Long> {
}