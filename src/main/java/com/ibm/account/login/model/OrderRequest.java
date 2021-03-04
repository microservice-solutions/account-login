package com.ibm.account.login.model;

import lombok.Data;

@Data
public class OrderRequest {
	private String productCode;
	private int productQuantity;
}
