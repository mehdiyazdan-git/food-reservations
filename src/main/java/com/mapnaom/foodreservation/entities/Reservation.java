package com.mapnaom.foodreservation.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Personnel personnel;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private FoodMenuItem foodMenuItem;

}
