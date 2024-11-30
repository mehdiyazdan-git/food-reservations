package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.searchForms.FoodSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class FoodSpecification {

    /**
     * Generates a Specification based on the provided FoodSearchForm.
     *
     * @param searchForm the form containing search criteria
     * @return Specification<Food> to be used with FoodRepository
     */
    public static Specification<Food> getFoodSpecification(FoodSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by ID if provided
            if (searchForm.getId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // Filter by name if provided
            if (searchForm.getName() != null && !searchForm.getName().isEmpty()) {
                predicates.add(hasName(searchForm.getName()).toPredicate(root, query, criteriaBuilder));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a Specification to filter Food entities by name.
     *
     * @param name the name to filter by
     * @return Specification<Food> for name filtering
     */
    public static Specification<Food> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }
}
