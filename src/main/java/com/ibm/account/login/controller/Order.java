package com.ibm.account.login.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.account.login.model.OrderRequest;
import com.ibm.account.login.model.TokenModel;
import com.ibm.account.login.repository.TokenRepository;
import com.ibm.account.login.service.PlaceOrderService;

@RestController
public class Order {
	
	@Autowired PlaceOrderService service;
	@Autowired TokenRepository tokenRepository;
	
	@PostMapping(path = "/order", consumes = "application/json")
	public ResponseEntity<String> order(
			@CookieValue(value = "SESSIONID", required = false) String requestSessionId,
			@RequestParam String username, @RequestBody List<OrderRequest> orderRequestList) {
		TokenModel token = getRepositoryModel(username);
		
		if ( token!=null && requestSessionId!=null && !requestSessionId.isEmpty() && token.getSessionId().equals(requestSessionId) ) {
			String authHeader = token.getTokenType() + " " + token.getIdToken();
			
			ResponseEntity<String> response = null;
			
			try {
				String transactionId = UUID.randomUUID().toString();
				response = service.placeOrder(authHeader, orderRequestList, transactionId);
				
				return response;
			} catch(Exception e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return new ResponseEntity<String>("User not authorized to perform transaction", HttpStatus.UNAUTHORIZED);
	}
	
	private TokenModel getRepositoryModel(String email) {
		TokenModel token = new TokenModel();
		token.setEmailId(email);
		
		ExampleMatcher ignoringExampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("EMAIL_ID", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
			      .withIgnorePaths("ACCESS_TOKEN", "TRANSACTION_TOKEN", "SESSIONID", "ID_TOKEN", "TOKEN_TYPE", "EXPIRES_IN");
		
		TokenModel result = null;
		
		Example<TokenModel> example = Example.of(token, ignoringExampleMatcher);
		
		Optional<TokenModel> models = tokenRepository.findOne(example);
		if (models.isPresent()) {
			result = models.get();
			
			return result;
		}
		
		return null;
	}
}
