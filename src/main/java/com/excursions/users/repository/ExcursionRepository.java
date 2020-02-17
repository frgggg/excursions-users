package com.excursions.users.repository;

import java.util.List;

public interface ExcursionRepository {
    List<Long> userTickets(Long id) throws IllegalStateException;
}
