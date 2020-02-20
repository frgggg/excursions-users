package com.excursions.users.service.impl;

import com.excursions.users.exception.ServiceException;
import com.excursions.users.repository.ExcursionRepository;
import com.excursions.users.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.excursions.users.log.message.ExcursionServiceLogMessages.EXCURSION_SERVICE_LOG_GET_USER_TICKETS_COUNT;

@Service
@Slf4j
public class ExcursionServiceImpl implements ExcursionService {

    private static final String SERVICE_NAME = "ExcursionServiceImpl";

    private ExcursionRepository excursionRepository;

    protected ExcursionServiceImpl(ExcursionRepository excursionRepository) {
        this.excursionRepository = excursionRepository;
    }

    @Override
    public Long userTicketsCount(Long id) {
        Long count;
        try {
            count = excursionRepository.userTicketsCount(id);
        } catch (IllegalStateException e) {
            throw new ServiceException(SERVICE_NAME, e.getMessage());
        }

        log.info(EXCURSION_SERVICE_LOG_GET_USER_TICKETS_COUNT, count, id);
        return count;
    }
}
