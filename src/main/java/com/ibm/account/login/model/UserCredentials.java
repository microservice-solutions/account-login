package com.ibm.account.login.model;

import lombok.Data;

@Data
public class UserCredentials {
	private String username;
	private String password;
	private String grant_type;
}
