package com.excursions.users.service.impl;

import com.excursions.users.exception.ServiceException;
import com.excursions.users.repository.ExcursionRepository;
import com.excursions.users.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.excursions.users.log.message.ExcursionServiceLogMessages.EXCURSION_SERVICE_LOG_GET_USER_TICKETS;

@Service
@Slf4j
public class ExcursionServiceImpl implements ExcursionService {

    private String SERVICE_NAME = "ExcursionServiceImpl";

    private ExcursionRepository excursionRepository;

    protected ExcursionServiceImpl(ExcursionRepository excursionRepository) {
        this.excursionRepository = excursionRepository;
    }

    @Override
    public List<Long> userTickets(Long id) {
        List<Long> tickets;
        try {
            tickets = excursionRepository.userTickets(id);
        } catch (IllegalStateException e) {
            throw new ServiceException(SERVICE_NAME, e.getMessage());
        }

        log.info(EXCURSION_SERVICE_LOG_GET_USER_TICKETS, id);
        return tickets;
    }
}
