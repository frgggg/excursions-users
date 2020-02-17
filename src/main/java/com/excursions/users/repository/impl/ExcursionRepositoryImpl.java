package com.excursions.users.repository.impl;

import com.excursions.users.client.ExcursionClient;
import com.excursions.users.repository.ExcursionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExcursionRepositoryImpl implements ExcursionRepository {

    private ExcursionClient excursionClient;

    @Autowired
    protected ExcursionRepositoryImpl(ExcursionClient excursionClient) {
        this.excursionClient = excursionClient;
    }

    @Override
    public List<Long> userTickets(Long id) {
        ResponseEntity<List<Long>> response;

        try {
            response = excursionClient.userTickets(id);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        if(response.getStatusCode() != HttpStatus.OK) {
            throw new IllegalStateException(response.getBody().toString());
        }

        List<Long> tickets = response.getBody();
        if(tickets != null) {
            return tickets;
        } else {
            return new ArrayList<>();
        }
    }
}
