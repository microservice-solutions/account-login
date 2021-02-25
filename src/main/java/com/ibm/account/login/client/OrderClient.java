package com.ibm.account.login.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "RECEIVE-ORDER", url = "http://localhost:9085/order")
public interface OrderClient {
	@PostMapping
	ResponseEntity<String> placeOrder(
			@RequestHeader(name = "Authorization") String bearerToken,
			@RequestParam(name = "productCode") String productCode,
			@RequestParam(name = "quantity") int quantity);
}
