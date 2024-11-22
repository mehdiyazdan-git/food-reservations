package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.DailyFoodOptions;
import com.mapnaom.foodreservation.entities.Personnel;
import com.mapnaom.foodreservation.entities.Reservation;
import com.mapnaom.foodreservation.searchForms.ReservationSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

public class ReservationSpecification {

    /**
     * ایجاد Specification برای جستجوی برنامه‌های رزرو بر اساس فیلدهای موجود در ReservationSearchForm
     *
     * @param searchForm فرم جستجو شامل فیلدهای id، personnelFirstName، personnelLastName، personnelCode و reservedFood
     * @return Specification<Reservation> برای اعمال فیلترها
     */
    public static Specification<Reservation> getReservationSpecification(ReservationSearchForm searchForm) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // فیلتر بر اساس شناسه رزرو
            if (searchForm.getId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("id"), searchForm.getId()));
            }

            // فیلتر بر اساس نام کوچک پرسنل
            if (searchForm.getPersonnelFirstName() != null && !searchForm.getPersonnelFirstName().isEmpty()) {
                Join<Reservation, Personnel> personnelJoin = root.join("personnel", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(personnelJoin.get("firstname")),
                                "%" + searchForm.getPersonnelFirstName().toLowerCase() + "%"
                        )
                );
            }

            // فیلتر بر اساس نام خانوادگی پرسنل
            if (searchForm.getPersonnelLastName() != null && !searchForm.getPersonnelLastName().isEmpty()) {
                Join<Reservation, Personnel> personnelJoin = root.join("personnel", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(personnelJoin.get("lastname")),
                                "%" + searchForm.getPersonnelLastName().toLowerCase() + "%"
                        )
                );
            }

            // فیلتر بر اساس کد پرسنل
            if (searchForm.getPersonnelCode() != null && !searchForm.getPersonnelCode().isEmpty()) {
                Join<Reservation, Personnel> personnelJoin = root.join("personnel", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(personnelJoin.get("code")),
                                "%" + searchForm.getPersonnelCode().toLowerCase() + "%"
                        )
                );
            }

            // فیلتر بر اساس نام غذای رزرو شده
            if (searchForm.getReservedFood() != null && !searchForm.getReservedFood().isEmpty()) {
                Join<Reservation, DailyFoodOptions> foodPlanJoin = root.join("dailyFoodOptions", JoinType.LEFT);
                Join<DailyFoodOptions, ?> foodJoin = foodPlanJoin.join("food", JoinType.LEFT);
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(foodJoin.get("name")),
                                "%" + searchForm.getReservedFood().toLowerCase() + "%"
                        )
                );
            }

            return predicate;
        };
    }
}
