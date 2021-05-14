package com.txacon.gap;

import com.google.common.collect.Lists;
import com.txacon.gap.application.api.CustomerService;
import com.txacon.gap.application.exceptions.CustomerNotFoundException;
import com.txacon.gap.domain.common.port.KeyEntityRepository;
import com.txacon.gap.domain.customer.entities.Customer;
import com.txacon.gap.domain.pricerange.entities.PriceRange;
import com.txacon.gap.domain.pricerange.port.PriceRangeRepository;
import com.txacon.gap.domain.rating.entities.AggregateRating;
import com.txacon.gap.domain.rating.port.RatingRepository;
import com.txacon.gap.domain.role.entities.Role;
import com.txacon.gap.domain.role.entities.RoleName;
import com.txacon.gap.domain.role.port.RoleRepository;
import com.txacon.gap.domain.tags.entities.TagName;
import com.txacon.gap.domain.tags.port.TagRepository;
import com.txacon.gap.infrastructure.rest.mapper.PasswordEncoderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class LoadMasterTablesCommandLineRunner implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final TagRepository tagRepository;
    private final RatingRepository ratingRepository;
    private final PriceRangeRepository priceRangeRepository;
    private final CustomerService customerService;
    private final PasswordEncoderMapper passwordEncoderMapper;

    private void insertTestUser() {
        Customer customer = createTestUser();
        createUser(customer);
    }

    private void createUser(Customer customer) {
        try {
            customerService.getByEmail(customer.getEmail());
        } catch (CustomerNotFoundException e) {
            customer.setPassword(passwordEncoderMapper.encodePassword(customer.getPassword()));
            customerService.addCustomer(customer);
        }
    }

    private Customer createTestUser() {
        Customer customer = new Customer();
        customer.setEmail("user@test.com");
        customer.setPassword("pass");
        customer.setFirstName("FirstName");
        customer.setLastName("LastName");
        customer.setRoles(Lists.newArrayList(Role.builder().role(RoleName.ROLE_USER).build(), Role.builder().role(RoleName.ROLE_SELLER).build()));
        customer.setUsername("User");
        customer.setActive(true);
        return customer;
    }

    private Customer createAdminUser() {
        Customer customer = new Customer();
        customer.setEmail("admin@test.com");
        customer.setPassword("pass");
        customer.setFirstName("FirstName");
        customer.setLastName("LastName");
        customer.setRoles(Collections.singletonList(Role.builder().role(RoleName.ROLE_ADMIN).build()));
        customer.setUsername("Admin");
        customer.setActive(true);
        return customer;
    }

    private <T extends Enum> void insertKeyEntity(KeyEntityRepository<T> keyEntityRepository, T... values) {
        Arrays.stream(values).forEach(e -> {
            if (!keyEntityRepository.findByName(e).isPresent()) {
                keyEntityRepository.save(e);
            }
        });
    }

    private void inserRoles() {
        Arrays.stream(RoleName.values()).forEach(e -> {
            if (!roleRepository.findByName(e).isPresent()) {
                roleRepository.save(Role.builder().role(e).build());
            }
        });
    }
}
