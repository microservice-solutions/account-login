package com.ibm.account.login.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ibm.account.login.client.OrderClient;

@Service
public class PlaceOrderService {
	
	@Autowired
	OrderClient client;
	
	public ResponseEntity<String> placeOrder(String bearerToken, String productCode, int quantity, String transactionId) {
		return client.placeOrder(bearerToken, productCode, quantity, transactionId);
	}
}
