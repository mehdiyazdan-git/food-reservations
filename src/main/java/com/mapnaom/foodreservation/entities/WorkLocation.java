package com.mapnaom.foodreservation.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "work_location")
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class WorkLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workLocationName;



    @OneToMany(mappedBy = "workLocation", orphanRemoval = true)
    private Set<FoodContractor> foodContractors = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "workLocation", orphanRemoval = true)
    private Set<Personnel> personnels = new LinkedHashSet<>();

    // Constructors
    public WorkLocation() {}

    public WorkLocation(String workLocationName) {
        this.workLocationName = workLocationName;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WorkLocation that = (WorkLocation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
