package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UserService {

    private RestTemplate restTemplate = new RestTemplate();
    private String BASE_URL;

    public UserService(String url) {

        this.BASE_URL = url;
    }


    public User[] userList(String token) {
        HttpEntity entity = getEntityFromToken(token);
        ResponseEntity<User[]> responseEntity = restTemplate.exchange(BASE_URL + "tenmo/users",
                HttpMethod.GET, entity, User[].class);
        return responseEntity.getBody();
    }


    public String getUserName(String token, int userId, String accountType) {
        HttpEntity entity = getEntityFromToken(token);
        ResponseEntity<String> responseEntity = restTemplate.exchange(BASE_URL + "tenmo/users/" + userId + "/" + accountType, HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }


    public HttpEntity getEntityFromToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);

        return entity;
    }
}
