package com.ibm.account.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ibm.account.login.model.TokenModel;

public interface TokenRepository extends JpaRepository<TokenModel, Long> {

}
