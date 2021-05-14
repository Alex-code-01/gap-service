package com.txacon.gap.infrastructure.db.jpa.pricerange.entities;

import com.txacon.gap.infrastructure.db.jpa.BaseEntity;
import com.txacon.gap.infrastructure.db.jpa.bussines.entites.BusinessEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity(name = "price_range")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString
public class PriceRangeEntity extends BaseEntity implements Serializable {

    @Id
    @Column(name = "price_range_id")
    private String id;
    @ToString.Exclude
    @Getter
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "priceRange")
    private Set<BusinessEntity> businesses = new HashSet<>();


}
