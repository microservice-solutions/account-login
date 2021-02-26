package com.ibm.account.login.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.account.login.model.TokenModel;
import com.ibm.account.login.repository.TokenRepository;
import com.ibm.account.login.service.PlaceOrderService;

@RestController("/order")
public class Order {
	
	@Autowired PlaceOrderService service;
	
	@Autowired TokenRepository tokenRepository;
	
	@PostMapping
	public ResponseEntity<String> order(@RequestParam String email, @RequestParam String productCode, @RequestParam int quantity) {
		TokenModel token = getRepositoryModel(email);
		
		if ( token!=null ) {
			String authHeader = token.getTokenType() + " " + token.getIdToken();
			
			ResponseEntity<String> response = null;
			
			try { 
				response = service.placeOrder(authHeader, productCode, quantity);
				
				return response;
			} catch(Exception e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return new ResponseEntity<String>("User not logged in", HttpStatus.UNAUTHORIZED);
	}
	
	private TokenModel getRepositoryModel(String email) {
		TokenModel token = new TokenModel();
		token.setEmailId(email);
		
		ExampleMatcher ignoringExampleMatcher = ExampleMatcher.matchingAny()
			      .withMatcher("EMAIL_ID", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
			      .withIgnorePaths("ACCESS_TOKEN", "ID_TOKEN", "TOKEN_TYPE", "EXPIRES_IN");
		
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
