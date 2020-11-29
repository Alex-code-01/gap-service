package com.txacon.gap.application.adapter;

import com.txacon.gap.application.api.CustomerService;
import com.txacon.gap.application.aspect.Loggable;
import com.txacon.gap.application.exceptions.ApiError;
import com.txacon.gap.application.exceptions.CustomerInvalidException;
import com.txacon.gap.application.exceptions.CustomerNotFoundException;
import com.txacon.gap.domain.customer.entities.Customer;
import com.txacon.gap.domain.customer.port.CustomerRepository;
import com.txacon.gap.domain.role.entities.RoleName;
import com.txacon.gap.domain.role.port.RoleRepository;
import com.txacon.gap.infrastructure.db.jpa.customer.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final RoleRepository roleRepository;
    private final CustomerMapper mapper;

    @Override
    @Loggable
    public Customer getById(Long customerId) {
        return repository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException(ApiError.ERROR_CUSTOMER_NOT_FOUND_BY_ID));
    }

    @Override
    @Loggable
    public Customer getByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException(ApiError.ERROR_CUSTOMER_NOT_FOUND_BY_EMAIL));
    }

    @Override
    @Loggable
    public Customer getByEmailAndPasswordHash(String email, String passwordHash) {
        return repository.findByEmailAndPasswordHash(email, passwordHash).orElseThrow(() -> new CustomerNotFoundException(ApiError.ERROR_CUSTOMER_NOT_FOUND_BY_EMAIL_AND_PASSWORD_HASH));
    }

    @Override
    @Loggable
    public void deleteById(Long customerId) {
        repository.deleteById(customerId);
    }

    @Override
    @Loggable
    public Customer update(Customer customer) {
        if (customer.getId() == null) throw new CustomerInvalidException(ApiError.ERROR_CUSTOMER_INVALID_TO_CREATE);
        Customer customerToUpd = repository.findById(customer.getId()).orElseThrow(() -> new CustomerNotFoundException(ApiError.ERROR_CUSTOMER_NOT_FOUND_BY_ID));
        mapper.updateCustomer(customer, customerToUpd);
        return repository.save(customerToUpd);
    }

    @Override
    public Customer addCustomer(Customer customer) {
        if (customer.getId() != null ||
                customer.getEmail() == null ||
                customer.getPasswordHash() == null)
            throw new CustomerInvalidException(ApiError.ERROR_CUSTOMER_INVALID_TO_CREATE);
        customer.setActive(true);
        if (customer.getRoles() == null || customer.getRoles().isEmpty()) {
            roleRepository.findByName(RoleName.ROLE_USER).ifPresent(role -> customer.setRoles(Arrays.asList(role)));
        }
        return repository.save(customer);
    }
}
