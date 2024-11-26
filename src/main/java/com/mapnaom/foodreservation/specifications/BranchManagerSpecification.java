package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.BranchManager;
import com.mapnaom.foodreservation.searchForms.BranchManagerSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class BranchManagerSpecification {

    public static Specification<BranchManager> getBranchManagerSpecification(BranchManagerSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by username if provided
            if (searchForm.getUsername() != null && !searchForm.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        "%" + searchForm.getUsername().toLowerCase() + "%"
                ));
            }

            // Filter by active status if provided
            if (searchForm.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), searchForm.getActive()));
            }

            // Filter by first name if provided
            if (searchForm.getFirstName() != null && !searchForm.getFirstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + searchForm.getFirstName().toLowerCase() + "%"
                ));
            }

            // Filter by last name if provided
            if (searchForm.getLastName() != null && !searchForm.getLastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + searchForm.getLastName().toLowerCase() + "%"
                ));
            }

            // Filter by branch name if provided
            if (searchForm.getBranchName() != null && !searchForm.getBranchName().isEmpty()) {
                // Join with the 'branch' association
                Join<Object, Object> branchJoin = root.join("branch", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(branchJoin.get("name")),
                        "%" + searchForm.getBranchName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
