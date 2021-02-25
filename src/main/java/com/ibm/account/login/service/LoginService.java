package com.ibm.account.login.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.account.login.client.LoginClient;
import com.ibm.account.login.model.UserCredentials;

@Service
public class LoginService {
	
	private static final Logger log = LoggerFactory.getLogger(LoginService.class);
	
	
	@Autowired
	LoginClient client;
	
	
	public ResponseEntity<JsonNode> getToken(String authorizationHeader, UserCredentials user) {
		log.info("Invoking get token client");
		
		return client.getToken(authorizationHeader, user);
	}
}
