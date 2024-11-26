package com.mapnaom.foodreservation.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BranchManager extends User {
    private String firstName;
    private String lastName;

    @OneToOne
    private Branch branch;
}

