package com.txacon.gap.application.adapter;

import com.txacon.gap.application.api.BusinessService;
import com.txacon.gap.application.aspect.Loggable;
import com.txacon.gap.application.exceptions.ApiError;
import com.txacon.gap.application.exceptions.BusinessInvalidException;
import com.txacon.gap.application.exceptions.BusinessNotFoundException;
import com.txacon.gap.domain.bussines.entities.Business;
import com.txacon.gap.domain.bussines.port.BusinessRepository;
import com.txacon.gap.domain.customer.entities.Customer;
import com.txacon.gap.domain.products.entities.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository repository;

    @Override
    @Loggable
    public List<Business> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Business> findByOwnerId(Long idOwner) {        
        return repository.findByOwnId(idOwner);
    }

    @Override
    public List<Business> findByOwner(Customer owner) {
        return repository.findByOwn(owner);
    }

    @Override
    @Loggable
    public Business findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(ApiError.ERROR_BUSINESS_NOT_FOUND_BY_ID));
    }

    @Override
    @Loggable
    public Business add(Business business, Long idOwner) {
        business.setId(null);
        business.setActive(true);
        return saveBussines(business, idOwner);
    }

    @Override
    @Loggable
    public Business update(Business business, Long idOwner) {
        if (business.getId() == null)
            throw new BusinessInvalidException(ApiError.ERROR_BUSINESS_INVALID_TO_UPDATE);
        return updateBussines(business, idOwner);
    }

    @Override
    @Loggable
    public void deleteById(Long businessId) {
        repository.deleteById(businessId);
    }

    @Override
    public Product addBussinessProduct(Long businessId, Product product) {
        Business business = repository.findById(businessId)
                .orElseThrow(() -> new BusinessInvalidException(ApiError.ERROR_BUSINESS_INVALID_TO_UPDATE));
        product.setId(null);
        product.setActive(true);
        business.getProducts().add(product);
        repository.update(business);
        return product;
    }

    @Override
    public Product updateBussinessProduct(Long bussinessId, Product product) {
        Business bussiness = findById(bussinessId);
        if (product.getId() == null)
            throw new BusinessInvalidException(ApiError.PRODUCT_INVALID_TO_UPDATE);
        Optional<Product> productToUpdate = bussiness.getProducts().stream().filter(e -> Objects.equals(e.getId(), product.getId())).findFirst();
        if (!productToUpdate.isPresent()) {
            throw new BusinessInvalidException(ApiError.ERROR_PRODUCT_NOT_FOUND);
        }
        updateProduct(productToUpdate.get(), product);
        repository.update(bussiness);
        return product;

    }

    private void updateProduct(Product toUpdate, Product product) {
        toUpdate.setWholeSalePrice(product.getWholeSalePrice());
        toUpdate.setRetailPrice(product.getRetailPrice());
        toUpdate.setName(product.getName());
        toUpdate.setDescription(product.getDescription());
        toUpdate.setPhotoLink(product.getPhotoLink());
    }

    @Override
    public void deleteProduct(Long businessId, Product product) {
        Business business = repository.findById(businessId)
                .orElseThrow(() -> new BusinessInvalidException(ApiError.ERROR_BUSINESS_INVALID_TO_UPDATE));
        if (product.getId() == null)
            throw new BusinessInvalidException(ApiError.PRODUCT_INVALID_TO_UPDATE);
        business.getProducts().removeIf(e -> e.getId().equals(product.getId()));
        repository.update(business);
    }

    private Business saveBussines(Business business, Long idOwner) {
        checkBusiness(business);
        business.setOwn(idOwner);
        return repository.save(business);
    }

    private Business updateBussines(Business business, Long idOwner) {
        checkBusiness(business);
        findById(business.getId());
        business.setOwn(idOwner);
        return repository.update(business);
    }

    private void checkBusiness(Business business) {
        if (Stream.of(business.getEmail(), business.getFiscalId(), business.getPhone(), business.getCity(),
                business.getZipcode(), business.getStreet1()).anyMatch(Objects::isNull))
            throw new BusinessInvalidException(ApiError.ERROR_BUSINESS_INVALID_TO_CREATE);
    }


}
