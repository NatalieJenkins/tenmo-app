package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Account;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AccountService {
    private RestTemplate restTemplate = new RestTemplate();
    private String BASE_URL;


    public AccountService(String url) {

        this.BASE_URL = url;
    }

    public Account getAccount(String token) {
        HttpEntity entity = getEntityFromToken(token);
        ResponseEntity<Account> responseEntity = restTemplate.exchange(BASE_URL + "tenmo/accounts",
                HttpMethod.GET, entity, Account.class);
        return responseEntity.getBody();
    }

    private HttpEntity getEntityFromToken (String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);

        return entity;
    }


}
