package com.txacon.gap.infrastructure.db.jpa.bussines.mapper;

import com.txacon.gap.domain.bussines.entities.Business;
import com.txacon.gap.infrastructure.db.jpa.GenericDomainMapper;
import com.txacon.gap.infrastructure.db.jpa.bussines.entites.BusinessEntity;
import com.txacon.gap.infrastructure.db.jpa.customer.entities.CustomerEntity;
import com.txacon.gap.infrastructure.db.jpa.payment.entities.PaymentMethodEntity;
import com.txacon.gap.infrastructure.db.jpa.payment.mapper.PaymentMethodMapper;
import com.txacon.gap.infrastructure.db.jpa.pricerange.mapper.PriceRangeMapper;
import com.txacon.gap.infrastructure.db.jpa.product.entities.ProductEntity;
import com.txacon.gap.infrastructure.db.jpa.product.mapper.ProductMapper;
import com.txacon.gap.infrastructure.db.jpa.rating.mapper.AggregateRatingMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LocalTimeMapper.class, AggregateRatingMapper.class, PriceRangeMapper.class,
        PaymentMethodMapper.class, ProductMapper.class})
public abstract class BusinessEntityMapper implements GenericDomainMapper<Business, BusinessEntity> {


    @Autowired
    private PaymentMethodMapper paymentMethodMapper;
    @Autowired
    private ProductMapper productMapper;

    public Long mapToId(CustomerEntity customer) {
        return Objects.isNull(customer) ? null : customer.getId();
    }

    public CustomerEntity mapToCustomer(Long id) {
        if (id == null)
            return null;
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(id);
        return customerEntity;
    }

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "aggregateRating", ignore = true)
    @Mapping(target = "priceRange", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    public abstract void updateBusinessWithoutProduct(Business business, @MappingTarget BusinessEntity entity);


    public void updateBussines(Business business, BusinessEntity entity){
        updateBusinessWithoutProduct(business,entity);
        updateBusinessProducts(business, entity);
        updateBusinessPaymentMethods(business,entity);
    }

    private void updateBusinessPaymentMethods(Business business, BusinessEntity entity){
        if (business.getProducts() == null){
            return;
        }
        Set<Long> containProducts = business.getProducts().stream().map(e -> e.getId()).collect(Collectors.toSet());
        Set<ProductEntity> toRemoveProducts = entity.getProducts().stream().filter(e -> !containProducts.contains(e.getId())).collect(Collectors.toSet());
        entity.getProducts().removeAll(toRemoveProducts);
        business.getPaymentMethods().forEach(payment -> {
            Optional<PaymentMethodEntity> productEntity = entity.getPaymentMethods().stream().filter(e -> e.getId().equals(payment.name())).findAny();
            if (!productEntity.isPresent()){
                PaymentMethodEntity paymentMethodEntity = paymentMethodMapper.toEntity(payment);
                paymentMethodEntity.getBusinesses().add(entity);
                entity.addPaymentMethod(paymentMethodEntity);
            }
        });
    }

    private void updateBusinessProducts(Business business, BusinessEntity entity) {
        if (business.getPaymentMethods() == null){
            return;
        }
        Set<String> containPayments = business.getPaymentMethods().stream().map(Enum::name).collect(Collectors.toSet());
        Set<PaymentMethodEntity> toRemove = entity.getPaymentMethods().stream().filter(e -> !containPayments.contains(e.getId())).collect(Collectors.toSet());
        entity.getPaymentMethods().removeAll(toRemove);
        business.getProducts().forEach(product -> {
            Optional<ProductEntity> productEntity = entity.getProducts().stream().filter(e -> e.getId().equals(product.getId())).findAny();
            if (productEntity.isPresent()){
                productMapper.updateProduct(product,productEntity.get());
            }
            else {
                ProductEntity newProduct = productMapper.toEntity(product);
                newProduct.setBusiness(entity);
                entity.getProducts().add(newProduct);
            }
        });
    }
}


