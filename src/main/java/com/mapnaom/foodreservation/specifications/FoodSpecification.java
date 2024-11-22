package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Food;
import com.mapnaom.foodreservation.searchForms.FoodSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class FoodSpecification {

    /**
     * ایجاد Specification برای جستجوی غذاها بر اساس فیلدهای موجود در FoodSearchForm
     *
     * @param searchForm فرم جستجو شامل فیلدهای id و name
     * @return Specification<Food> برای اعمال فیلترها
     */
    public static Specification<Food> getFoodSpecification(FoodSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // فیلتر بر اساس شناسه غذا
            if (searchForm.getId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // فیلتر بر اساس نام غذا
            if (searchForm.getName() != null && !searchForm.getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("name")),
                                "%" + searchForm.getName().toLowerCase() + "%"
                        ));
            }

            return predicate;
        };
    }
}
