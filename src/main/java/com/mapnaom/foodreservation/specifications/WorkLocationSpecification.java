package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.WorkLocation;
import com.mapnaom.foodreservation.searchForms.WorkLocationSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class WorkLocationSpecification {

    /**
     * ایجاد Specification برای جستجوی محل‌های کاری بر اساس فیلدهای موجود در WorkLocationSearchForm
     *
     * @param searchForm فرم جستجو شامل فیلدهای id و workLocationName
     * @return Specification<WorkLocation> برای اعمال فیلترها
     */
    public static Specification<WorkLocation> getWorkLocationSpecification(WorkLocationSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // فیلتر بر اساس شناسه محل کاری
            if (searchForm.getId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // فیلتر بر اساس نام محل کاری
            if (searchForm.getWorkLocationName() != null && !searchForm.getWorkLocationName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("workLocationName")),
                                "%" + searchForm.getWorkLocationName().toLowerCase() + "%"
                        ));
            }

            return predicate;
        };
    }
}
