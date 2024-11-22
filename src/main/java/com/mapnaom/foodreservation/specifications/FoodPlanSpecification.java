package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import com.mapnaom.foodreservation.searchForms.FoodPlanSearchForm;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class FoodPlanSpecification {

    public static Specification<DailyFoodOptions> getFoodPlanSpecification(FoodPlanSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (searchForm.getId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            if (searchForm.getFoodName() != null && !searchForm.getFoodName().isEmpty()) {
                Join<Object, Object> foodJoin = root.join("food", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(foodJoin.get("name")), "%" + searchForm.getFoodName().toLowerCase() + "%"));
            }

            if (searchForm.getLocalDate() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("localDate"), searchForm.getLocalDate()));
            }

            return predicate;
        };
    }
}
