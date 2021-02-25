package com.ibm.account.login.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.account.login.model.UserCredentials;

@FeignClient(name = "ACCOUNT-LOGIN", url = "https://us-south.appid.cloud.ibm.com/")
public interface LoginClient {
	
	@RequestMapping(value = "/oauth/v4/83f91118-37d1-4c0d-b0b8-4a530d1174bb/token", produces = { "*/*" }, method = RequestMethod.POST)
	
	ResponseEntity<JsonNode> getToken(
			@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody UserCredentials user);
}
