package com.mapnaom.foodreservation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Employee extends User {
    private String firstName;
    private String lastName;
    private String employeeCode;

    @ManyToOne
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_employee_branch"))
    private Branch branch;
}
