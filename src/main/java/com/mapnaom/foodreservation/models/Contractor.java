package com.mapnaom.foodreservation.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Contractor extends User {
    private String name;

    @ManyToOne
    @JoinColumn(name = "branch_id", foreignKey = @ForeignKey(name = "fk_contractor_branch"))
    private Branch branch;

    @OneToMany(mappedBy = "contractor")
    private Set<Menu> menus = new LinkedHashSet<>();
}
