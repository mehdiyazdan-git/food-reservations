package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.WorkLocation;
import com.mapnaom.foodreservation.searchForms.PersonnelSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class PersonnelSpecification {

    /**
     * ایجاد Specification برای جستجوی پرسنل بر اساس فیلدهای موجود در PersonnelSearchForm
     *
     * @param searchForm فرم جستجو شامل فیلدهای id، firstname، lastname، code و workLocationName
     * @return Specification<Personnel> برای اعمال فیلترها
     */
    public static Specification<Personnel> getPersonnelSpecification(PersonnelSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // فیلتر بر اساس شناسه پرسنل
            if (searchForm.getId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // فیلتر بر اساس نام کوچک پرسنل
            if (searchForm.getFirstname() != null && !searchForm.getFirstname().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstname")),
                                "%" + searchForm.getFirstname().toLowerCase() + "%"
                        ));
            }

            // فیلتر بر اساس نام خانوادگی پرسنل
            if (searchForm.getLastname() != null && !searchForm.getLastname().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastname")),
                                "%" + searchForm.getLastname().toLowerCase() + "%"
                        ));
            }

            // فیلتر بر اساس کد پرسنل
            if (searchForm.getCode() != null && !searchForm.getCode().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("code")),
                                "%" + searchForm.getCode().toLowerCase() + "%"
                        ));
            }

            // فیلتر بر اساس نام محل کاری
            if (searchForm.getWorkLocationName() != null && !searchForm.getWorkLocationName().isEmpty()) {
                Join<Personnel, WorkLocation> workLocationJoin = root.join("workLocation", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(workLocationJoin.get("workLocationName")),
                                "%" + searchForm.getWorkLocationName().toLowerCase() + "%"
                        ));
            }

            return predicate;
        };
    }
}
