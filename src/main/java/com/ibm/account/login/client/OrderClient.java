package com.ibm.account.login.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibm.account.login.model.OrderRequest;

@FeignClient(name = "RECEIVE-ORDER", url = "http://localhost:9085/order")
public interface OrderClient {
	@PostMapping
	ResponseEntity<String> placeOrder(
			@RequestHeader(name = "Authorization") String bearerToken,
			@RequestBody List<OrderRequest> orderRequestList,
			@RequestParam(name = "transactionId") String transactionId);
}
