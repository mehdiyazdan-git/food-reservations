package com.mapnaom.foodreservation.specifications;

import com.mapnaom.foodreservation.entities.Order;
import com.mapnaom.foodreservation.entities.Employee;
import com.mapnaom.foodreservation.entities.Branch;
import com.mapnaom.foodreservation.entities.FoodOption;
import com.mapnaom.foodreservation.searchForms.OrderSearchForm;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    public static Specification<Order> getOrderSpecification(OrderSearchForm searchForm) {
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

            // Filter by status if provided
            if (searchForm.getStatus() != null && !searchForm.getStatus().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("status")),
                        searchForm.getStatus().toLowerCase()
                ));
            }

            // Join with Employee
            Join<Order, Employee> employeeJoin = null;
            if (searchForm.getEmployeeFirstName() != null && !searchForm.getEmployeeFirstName().isEmpty()
                || searchForm.getEmployeeLastName() != null && !searchForm.getEmployeeLastName().isEmpty()
                || searchForm.getEmployeeBranchName() != null && !searchForm.getEmployeeBranchName().isEmpty()) {

                employeeJoin = root.join("employee", JoinType.LEFT);

                // Filter by Employee's first name
                if (searchForm.getEmployeeFirstName() != null && !searchForm.getEmployeeFirstName().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(employeeJoin.get("firstName")),
                            "%" + searchForm.getEmployeeFirstName().toLowerCase() + "%"
                    ));
                }

                // Filter by Employee's last name
                if (searchForm.getEmployeeLastName() != null && !searchForm.getEmployeeLastName().isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(employeeJoin.get("lastName")),
                            "%" + searchForm.getEmployeeLastName().toLowerCase() + "%"
                    ));
                }

                // Filter by Employee's branch name
                if (searchForm.getEmployeeBranchName() != null && !searchForm.getEmployeeBranchName().isEmpty()) {
                    // Join with Employee's Branch
                    Join<Employee, Branch> branchJoin = employeeJoin.join("branch", JoinType.LEFT);
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(branchJoin.get("name")),
                            "%" + searchForm.getEmployeeBranchName().toLowerCase() + "%"
                    ));
                }
            }

            // Join with FoodOption
            if (searchForm.getFoodOptionName() != null && !searchForm.getFoodOptionName().isEmpty()) {
                Join<Order, FoodOption> foodOptionJoin = root.join("foodOption", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(foodOptionJoin.get("name")),
                        "%" + searchForm.getFoodOptionName().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
