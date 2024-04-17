package com.microservice.seller.service;

import com.microservice.seller.model.Seller;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationService {

    public static Specification<Seller> hasId(Integer providedId) {
        System.out.println("hello111");
        return (Root<Seller> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), providedId);
    }
    public static Specification<Seller>hasName(String providedName){
        System.out.println("hello");
       return (Root<Seller> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
               criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + providedName.toLowerCase() + "%");
    }

}
