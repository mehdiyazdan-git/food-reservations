package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Menu;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.entities.Contractor;
import com.mapnaom.foodreservation.searchForms.MenuSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class MenuSpecification {

    public static Specification<Menu> getMenuSpecification(MenuSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by date if provided
            if (searchForm.getDate() != null) {
                predicates.add(criteriaBuilder.equal(root.get("date"), searchForm.getDate()));
            }
            if (searchForm.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), searchForm.getStartDate()));
            }
            if (searchForm.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), searchForm.getEndDate()));
            }


            // Filter by Menu's branch name if provided
            if (searchForm.getBranchName() != null && !searchForm.getBranchName().isEmpty()) {
                // Join to branch
                Join<Menu, Branch> branchJoin = root.join("branch", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(branchJoin.get("name")),
                        "%" + searchForm.getBranchName().toLowerCase() + "%"
                ));
            }

            // Filter by contractor's name if provided
            if (searchForm.getContractorName() != null && !searchForm.getContractorName().isEmpty()) {
                // Join to contractor
                Join<Menu, Contractor> contractorJoin = root.join("contractor", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(contractorJoin.get("name")),
                        "%" + searchForm.getContractorName().toLowerCase() + "%"
                ));
            }

            // Filter by contractor's branch name if provided
            if (searchForm.getContractorBranchName() != null && !searchForm.getContractorBranchName().isEmpty()) {
                // Join to contractor
                Join<Menu, Contractor> contractorJoin = root.join("contractor", JoinType.LEFT);
                // Join from contractor to contractor's branch
                Join<Contractor, Branch> contractorBranchJoin = contractorJoin.join("branch", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(contractorBranchJoin.get("name")),
                        "%" + searchForm.getContractorBranchName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
