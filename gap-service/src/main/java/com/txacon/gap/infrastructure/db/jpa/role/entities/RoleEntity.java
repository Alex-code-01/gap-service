package com.txacon.gap.infrastructure.db.jpa.role.entities;

import com.txacon.gap.infrastructure.db.jpa.BaseEntity;
import com.txacon.gap.infrastructure.db.jpa.customer.entities.CustomerEntity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "role_id")
@Table(name = "role")
public class RoleEntity extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "name", unique = true)
    private String name;
    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private Set<CustomerEntity> customers = new HashSet<>();


    public Set<CustomerEntity> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<CustomerEntity> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
    }

    public void addCustomer(CustomerEntity customerEntity) {
        this.customers.add(customerEntity);
    }
}
