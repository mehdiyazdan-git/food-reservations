package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.models.FoodOption;
import com.mapnaom.foodreservation.models.Menu;
import com.mapnaom.foodreservation.models.Branch;
import com.mapnaom.foodreservation.searchForms.FoodOptionSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class FoodOptionSpecification {

    public static Specification<FoodOption> getFoodOptionSpecification(FoodOptionSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by name if provided
            if (searchForm.getName() != null && !searchForm.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + searchForm.getName().toLowerCase() + "%"
                ));
            }

            // Filter by price if provided
            if (searchForm.getPrice() != null) {
                predicates.add(criteriaBuilder.equal(root.get("price"), searchForm.getPrice()));
            }

            // Join with Menu if menu-related filters are provided
            Join<FoodOption, Menu> menuJoin = null;
            boolean menuJoinNeeded = searchForm.getMenuDate() != null ||
                                     searchForm.getStartDate() != null ||
                                     searchForm.getEndDate() != null ||
                                     (searchForm.getMenuBranchName() != null && !searchForm.getMenuBranchName().isEmpty());

            if (menuJoinNeeded) {
                menuJoin = root.join("menu", JoinType.LEFT);

                // Filter by menu date if provided
                if (searchForm.getMenuDate() != null) {
                    predicates.add(criteriaBuilder.equal(menuJoin.get("date"), searchForm.getMenuDate()));
                }

                // Filter by start date if provided
                if (searchForm.getStartDate() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(menuJoin.get("date"), searchForm.getStartDate()));
                }

                // Filter by end date if provided
                if (searchForm.getEndDate() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(menuJoin.get("date"), searchForm.getEndDate()));
                }

                // Filter by menu's branch name if provided
                if (searchForm.getMenuBranchName() != null && !searchForm.getMenuBranchName().isEmpty()) {
                    // Join with Branch
                    Join<Menu, Branch> branchJoin = menuJoin.join("branch", JoinType.LEFT);
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(branchJoin.get("name")),
                            "%" + searchForm.getMenuBranchName().toLowerCase() + "%"
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
