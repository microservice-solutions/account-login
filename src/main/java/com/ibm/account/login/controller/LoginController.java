package com.ibm.account.login.controller;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.ibm.account.login.model.TokenModel;
import com.ibm.account.login.model.UserCredentials;
import com.ibm.account.login.repository.TokenRepository;
import com.ibm.account.login.service.LoginService;

@Component
@RestController
public class LoginController {
	
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired TokenRepository 		tokenRepository;
	@Autowired LoginService 		service;
	
	@Value("${app.client.id}")
	private String			CLIENT_ID;
	
	@Value("${app.client.secret}")
	private String 			CLIENT_SECRET;
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, 
			@CookieValue(value = "SESSIONID", required = false) String requestSessionId,
			HttpServletResponse httpResponse) throws UnsupportedEncodingException {
		
		log.info("Received login request for user {}", username);
		
		TokenModel repoModel = getRepositoryModel(username);
		
		if ( repoModel==null ) {
			UserCredentials user = new UserCredentials();
			user.setGrant_type("password");
			user.setUsername(username);
			user.setPassword(password);
			
			String authHeader = "Basic " + Base64.getEncoder().encodeToString((CLIENT_ID+":"+CLIENT_SECRET).getBytes("utf-8"));
			UUID sessionId = UUID.randomUUID();
			
			ResponseEntity<JsonNode> response = null;
			
			try {
				response = service.getToken(authHeader, user);
			} catch (Exception e) {
				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			String accessToken = response.getBody().findValue("access_token").asText();
			String idToken = response.getBody().findValue("id_token").asText();
			String tokenType = response.getBody().findValue("token_type").asText();
			Long expiresIn = response.getBody().findValue("expires_in").asLong();
			
			Cookie cookie = new Cookie("SESSIONID", sessionId.toString());
			cookie.setPath("/");
			cookie.setHttpOnly(true);
			httpResponse.addCookie(cookie);
			
			
			TokenModel token = new TokenModel();
			token.setEmailId(username);
			token.setAccessToken(accessToken);
			token.setIdToken(idToken);
			token.setSessionId(sessionId.toString());
			token.setTokenType(tokenType);
			token.setExpiresIn(expiresIn);
			
			tokenRepository.save(token);
			
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("User login successful");
		} else if ( repoModel!=null && repoModel.getSessionId().equals(requestSessionId) ) {
			return ResponseEntity
					.status(HttpStatus.OK)
					.body("User already logged in");
		}
		
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body("User not authorized");
	}
	
	@PostMapping("/revoke")
	public ResponseEntity<String> revoke(@RequestParam String username) {
		log.info("Received revoke request for user {}", username);
		
		TokenModel repoModel = getRepositoryModel(username);
		
		if ( repoModel!=null ) {
			tokenRepository.delete(repoModel);
			
			return ResponseEntity.ok("User access revoked");
		}
		
		return ResponseEntity.ok("User does not have valid authorization");
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
