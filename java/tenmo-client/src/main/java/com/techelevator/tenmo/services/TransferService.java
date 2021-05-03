package com.techelevator.tenmo.services;

import com.techelevator.tenmo.models.Transfer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TransferService {
    private RestTemplate restTemplate = new RestTemplate();
    private String BASE_URL;


    public TransferService(String url) {

        this.BASE_URL = url;
    }

    public Transfer[] transfersList(String token) {
        HttpEntity entity = getEntityFromToken(token);
        ResponseEntity<Transfer[]> responseEntity = restTemplate.exchange(BASE_URL + "tenmo/transfers",
                HttpMethod.GET, entity, Transfer[].class);
        Transfer[] transfers = responseEntity.getBody();

        return transfers;
    }

    public Transfer transfer(String token, int toUser, BigDecimal amount) {
        Transfer transfer = new Transfer(toUser, amount);
        HttpEntity<Transfer> entity = makeTransferEntity(token, transfer);
        ResponseEntity<Transfer> responseEntity= restTemplate.exchange
                (BASE_URL + "tenmo/transfers",
                        HttpMethod.PUT, entity, Transfer.class);
        return responseEntity.getBody();
    }

    public Transfer getTransferDetails(String token, int id){
        Transfer transfer = new Transfer(id);
        HttpEntity<Transfer> entity = makeTransferEntity(token, transfer);
        ResponseEntity<Transfer> responseEntity = restTemplate.exchange(BASE_URL +
                        "tenmo/transfers/" + id, HttpMethod.GET, entity, Transfer.class);
        return responseEntity.getBody();
    }

    private HttpEntity<Transfer> makeTransferEntity (String token, Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        return entity;
    }

    private HttpEntity getEntityFromToken (String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity entity = new HttpEntity(headers);

        return entity;
    }
}
