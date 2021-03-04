package com.ibm.account.login.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ibm.account.login.client.OrderClient;
import com.ibm.account.login.model.OrderRequest;

@Service
public class PlaceOrderService {
	
	@Autowired
	OrderClient client;
	
	public ResponseEntity<String> placeOrder(String bearerToken, List<OrderRequest> orderRequestList, String transactionId) {
		return client.placeOrder(bearerToken, orderRequestList, transactionId);
	}
}
