package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Contractor;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.searchForms.ContractorSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class ContractorSpecification {

    public static Specification<Contractor> getContractorSpecification(ContractorSearchForm searchForm) {
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

            // Filter by name if provided
            if (searchForm.getName() != null && !searchForm.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchForm.getName().toLowerCase() + "%"
                ));
            }

            // Filter by branch name if provided
            if (searchForm.getBranchName() != null && !searchForm.getBranchName().isEmpty()) {
                // Join with the 'branch' association
                Join<Contractor, Branch> branchJoin = root.join("branch", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(branchJoin.get("name")),
                        "%" + searchForm.getBranchName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
