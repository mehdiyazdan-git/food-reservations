package com.mapnaom.foodreservation.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class FoodOption {
    @Id
    private Long id;
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "menu_id",foreignKey = @ForeignKey(name = "fk_food_option_menu"))
    private Menu menu;

    @OneToMany(mappedBy = "foodOption")
    @ToString.Exclude
    private Set<Order> orders;

    @ManyToOne
    @JoinColumn(name = "food_id",foreignKey = @ForeignKey(name = "fk_food_option_food"))
    private Food food;
    // equals and hashCode methods
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FoodOption that = (FoodOption) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
