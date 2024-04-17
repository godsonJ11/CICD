package com.microservice.seller.service;

import com.microservice.seller.model.Seller;
import com.microservice.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecificationSearch {
    private final SellerRepository sellerRepository;

    public Page<Seller> criteriaSearch(Seller seller, Pageable pageable){
        Specification<Seller> spec= Specification.where(null);//this is used for dynamic query
        if(seller.getId()!=null && seller.getId()>0){
           spec=spec.and(SpecificationService.hasId(seller.getId()));
        }
        if(seller.getName()!=null && !seller.getName().isBlank()){
            spec=spec.and(SpecificationService.hasName(seller.getName()));
        }
        return sellerRepository.findAll(spec,pageable);
    }
}
