package com.excursions.users.repository.impl;

import com.excursions.users.client.ExcursionClient;
import com.excursions.users.repository.ExcursionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExcursionRepositoryImpl implements ExcursionRepository {

    private ExcursionClient excursionClient;

    @Autowired
    protected ExcursionRepositoryImpl(ExcursionClient excursionClient) {
        this.excursionClient = excursionClient;
    }

    @Override
    public Long userTicketsCount(Long id) {
        ResponseEntity<Long> response;

        try {
            response = excursionClient.userTickets(id);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        HttpStatus status = response.getStatusCode();
        Long count = response.getBody();

        if((status != HttpStatus.OK) || (count == null)) {
            throw new IllegalStateException(response.getBody().toString());
        }

        return count;
    }
}
