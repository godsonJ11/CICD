package com.microservice.seller.client;

import com.microservice.seller.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "ADMIN")
public interface AdminClient {
    @GetMapping("/api/admin/fetch")
    ResponseEntity<Response> getAllAdminDetails();
}
