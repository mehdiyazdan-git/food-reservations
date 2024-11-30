package com.mapnaom.foodreservation.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "food", orphanRemoval = true)
    private List<FoodOption> foodOptions = new ArrayList<>();

    public Food(String name) {
        this.name = name;
    }
}
