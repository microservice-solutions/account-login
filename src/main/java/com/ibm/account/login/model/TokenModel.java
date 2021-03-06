package com.ibm.account.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class TokenModel {
	@Id
	@GeneratedValue
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "EMAIL_ID", unique = true)
	private String emailId;
	
	@Column(name = "ACCESS_TOKEN", length = 2000)
	private String accessToken;
	
	@Column(name = "SESSIONID", length = 500)
	private String sessionId;
	
	@Column(name = "ID_TOKEN", length = 2000)
	private String idToken;
	
	@Column(name = "TOKEN_TYPE")
	private String tokenType;
	
	@Column(name = "EXPIRES_IN")
	private Long expiresIn;
}
