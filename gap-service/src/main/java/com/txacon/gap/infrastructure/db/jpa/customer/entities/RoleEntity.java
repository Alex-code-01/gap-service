package com.txacon.gap.infrastructure.db.jpa.customer.entities;

import com.txacon.gap.infrastructure.db.jpa.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "role_id")
@Table(name = "role")
public class RoleEntity extends BaseEntity {

    @ManyToMany(mappedBy = "roles")
    Set<CustomerEntity> customers = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "name", unique = true)
    private String name;


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
