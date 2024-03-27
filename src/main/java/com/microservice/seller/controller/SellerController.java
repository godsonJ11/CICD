package com.microservice.seller.controller;

import com.microservice.seller.client.AdminClient;
import com.microservice.seller.model.Seller;
import com.microservice.seller.repository.SellerRepository;
import com.microservice.seller.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/seller")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

    private final SellerRepository sellerRepository;



    @PostMapping("register")
    public ResponseEntity<Response> saveAdminDetails(@RequestBody Seller seller) {
        Response response = new Response();
        log.info("trace id {}", MDC.get("traceId"));
        Assert.notNull(seller, "Admin model is null");
        sellerRepository.save(seller);
        response.setData(seller);
        return ResponseEntity.ok(response);
    }

    @GetMapping("fetch")
    public ResponseEntity<Response> getAllAdminDetails() {
        Response response = new Response();
        response.setData(sellerRepository.findAll());
        return ResponseEntity.ok(response);
    }

    @GetMapping("fetchById/{id}")
    public ResponseEntity<Response> findById(@PathVariable("id") Integer id) {
        Response response = new Response();
        response.setData(sellerRepository.findById(id));
        return ResponseEntity.ok(response);
    }

//    @GetMapping("calling")
//    public void callingTheOtherServices() {
//        System.out.println(adminClient.getAllAdminDetails().getBody().toString());
//    }

}
