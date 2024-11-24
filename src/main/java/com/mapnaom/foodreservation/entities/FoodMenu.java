package com.mapnaom.foodreservation.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "food_menu")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class FoodMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "local_date")
    private LocalDate localDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "foodMenu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodMenuItem> foodMenuItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "food_contractor_id")
    private FoodContractor foodContractor;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FoodMenu foodMenu = (FoodMenu) o;
        return getId() != null && Objects.equals(getId(), foodMenu.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
