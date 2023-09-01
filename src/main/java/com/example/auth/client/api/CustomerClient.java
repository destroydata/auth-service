package com.example.auth.client.api;

import com.example.auth.client.request.OwnerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("CUSTOMER-SERVICE")
public interface CustomerClient {
    @PostMapping("/api/v1/customer")
    ResponseEntity<Void> saveCustomer(OwnerRequest ownerRequest);

}
