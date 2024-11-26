package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.searchForms.BranchSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class BranchSpecification {

    public static Specification<Branch> getBranchSpecification(BranchSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by name if provided
            if (searchForm.getName() != null && !searchForm.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchForm.getName().toLowerCase() + "%"));
            }

            // Filter by code if provided
            if (searchForm.getCode() != null && !searchForm.getCode().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), "%" + searchForm.getCode().toLowerCase() + "%"));
            }

            if (searchForm.getActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("active"), searchForm.getActive()));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Branch> hasName(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }
}
